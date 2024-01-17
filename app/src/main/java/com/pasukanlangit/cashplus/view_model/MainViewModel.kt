package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.dto.TopUpResponseDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.ArrayDeque
import java.util.Queue

class MainViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val context: Context
) : ViewModel() {

    private val _profileLoading = MutableStateFlow(false)
    val profileLoading: StateFlow<Boolean> = _profileLoading
    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile: StateFlow<ProfileResponse?> = _profile
    private val _profileError = MutableStateFlow(ArrayDeque<String>())
    val profileError: StateFlow<Queue<String>> = _profileError

    private val _referralLoading = MutableStateFlow(false)
    val referralLoading: StateFlow<Boolean> = _referralLoading
    private val _changeReferral = Channel<UserModel?>()
    val changeReferral = _changeReferral.receiveAsFlow()
    private val _referralError = MutableStateFlow(ArrayDeque<String>())
    val referralError: StateFlow<Queue<String>> = _referralError

    private val _logoutLoading = MutableStateFlow(false)
    val logoutLoading: StateFlow<Boolean> = _logoutLoading
    private val _logout = Channel<UserModel?>()
    val logout = _logout.receiveAsFlow()
    private val _logoutError = MutableStateFlow(ArrayDeque<String>())
    val logoutError: StateFlow<Queue<String>> = _logoutError

    private val _topUpQrisLoading = MutableStateFlow(false)
    val topUpQrisLoading: StateFlow<Boolean> = _topUpQrisLoading
    private val _topUpQris = Channel<TopUpResponseDto?>()
    val topUpQris = _topUpQris.receiveAsFlow()
    private val _topUpQrisError = MutableStateFlow(ArrayDeque<String>())
    val topUpQrisError: StateFlow<Queue<String>> = _topUpQrisError

    private val _uploadPhotoLoading = MutableStateFlow(false)
    val uploadPhotoLoading: StateFlow<Boolean> = _uploadPhotoLoading
    private val _uploadPhoto = Channel<UserModel?>()
    val uploadPhoto = _uploadPhoto.receiveAsFlow()
    private val _uploadPhotoError = MutableStateFlow(ArrayDeque<String>())
    val uploadPhotoError: StateFlow<Queue<String>> = _uploadPhotoError

    private val _updateEmailLoading = MutableStateFlow(false)
    val updateEmailLoading: StateFlow<Boolean> = _updateEmailLoading
    private val _updateEmail = Channel<UserModel?>()
    val updateEmail = _updateEmail.receiveAsFlow()
    private val _updateEmailError = MutableStateFlow(ArrayDeque<String>())
    val updateEmailError: StateFlow<Queue<String>> = _updateEmailError

    fun getProfile(uuid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getProfile(uuid = uuid, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _profile.value = data }
                            it.message?.let { error -> appendProfileError(error) }
                            _profileLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendProfileError(context.getString(R.string.exception_network))
            }
        })
    }

    fun changeReferral(uuid: String, referral: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .changeReferral(uuid = uuid, referral = referral, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _changeReferral.send(data) }
                            it.message?.let { error -> appendReferralError(error) }
                            _referralLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendReferralError(context.getString(R.string.exception_network))
            }
        })
    }

    fun logout(uuid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .logOut(uuid = uuid, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _logout.send(data) }
                            it.message?.let { error -> appendLogoutError(error) }
                            _logoutLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendLogoutError(context.getString(R.string.exception_network))
            }
        })
    }

    fun topUpQris(request: TopUpQrisRequest, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .topUpQris(request = request, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _topUpQris.send(data) }
                            it.message?.let { error -> appendTopUpQrisError(error) }
                            _topUpQrisLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendTopUpQrisError(context.getString(R.string.exception_network))
            }
        })
    }

    fun uploadPhotoProfile(request: KYCRequest, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .uploadPhotoProfile(request = request, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _uploadPhoto.send(data) }
                            it.message?.let { error -> appendUploadPhotoError(error) }
                            _uploadPhotoLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendUploadPhotoError(context.getString(R.string.exception_network))
            }
        })
    }

    fun updateEmail(uuid: String, email: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .updateEMail(uuid = uuid, email = email, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _updateEmail.send(data) }
                            it.message?.let { error -> appendUpdateEmailError(error) }
                            _updateEmailLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendUpdateEmailError(context.getString(R.string.exception_network))
            }
        })
    }

    private fun appendProfileError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _profileError.value = newMessage
    }
    fun removeProfileError() {
        val currentMessage = profileError.value
        currentMessage.remove()
        _profileError.value = ArrayDeque(emptyList())
    }

    private fun appendReferralError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _referralError.value = newMessage
    }
    fun removeReferralError() {
        val currentMessage = referralError.value
        currentMessage.remove()
        _referralError.value = ArrayDeque(emptyList())
    }

    private fun appendLogoutError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _logoutError.value = newMessage
    }
    fun removeLogoutError() {
        val currentMessage = logoutError.value
        currentMessage.remove()
        _logoutError.value = ArrayDeque(emptyList())
    }

    private fun appendTopUpQrisError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _topUpQrisError.value = newMessage
    }
    fun removeTopUpQrisError() {
        val currentMessage = topUpQrisError.value
        currentMessage.remove()
        _topUpQrisError.value = ArrayDeque(emptyList())
    }

    private fun appendUploadPhotoError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _uploadPhotoError.value = newMessage
    }
    fun removeUploadPhotoError() {
        val currentMessage = uploadPhotoError.value
        currentMessage.remove()
        _uploadPhotoError.value = ArrayDeque(emptyList())
    }

    private fun appendUpdateEmailError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _updateEmailError.value = newMessage
    }
    fun removeUpdateEmailError() {
        val currentMessage = updateEmailError.value
        currentMessage.remove()
        _updateEmailError.value = ArrayDeque(emptyList())
    }
}