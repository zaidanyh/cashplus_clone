package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.response.UserModel
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.OtpResponse
import com.pasukanlangit.cashplus.domain.model.LoginRequest
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.ResetPwCodeRequest
import com.pasukanlangit.cashplus.model.request_body.UpdateFirebaseTokenRequest
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class LoginViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val mContext: Context
) : ViewModel() {

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading
    private val _login = Channel<UserModel?>()
    val login = _login.receiveAsFlow()
    private val _loginByOtp = Channel<OtpResponse?>()
    val loginByOtp = _loginByOtp.receiveAsFlow()
    private val _loginError = MutableStateFlow(ArrayDeque<String>())
    val loginError: StateFlow<Queue<String>> = _loginError

    private val _otpLoading = MutableStateFlow(false)
    val otpLoading: StateFlow<Boolean> = _otpLoading
    private val _otpLogin = MutableStateFlow<UserModel?>(null)
    val otpLogin: StateFlow<UserModel?> = _otpLogin
    private val _otpError = MutableStateFlow(ArrayDeque<String>())
    val otpError: StateFlow<Queue<String>> = _otpError

    private val _updateFcmLoading = MutableStateFlow(false)
    val updateFcmLoading: StateFlow<Boolean> = _updateFcmLoading
    private val _updateFcm = Channel<Boolean?>()
    val updateFcm = _updateFcm.receiveAsFlow()
    private val _updateFcmError = MutableStateFlow(ArrayDeque<String>())
    val updateFcmError: StateFlow<Queue<String>> = _updateFcmError

    fun login(loginRequest: LoginRequest, signature: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .loginUseCase(loginRequest = loginRequest, signature = signature)
                        .onEach {
                            it.data?.let { data -> _login.send(data) }
                            it.message?.let { error -> appendErrorLogin(error) }
                            _loginLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorLogin(mContext.getString(R.string.exception_network))
            }
        })
    }

    fun loginByOtp(loginRequest: LoginRequest) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .loginByOtpUseCase(loginRequest)
                        .onEach {
                            it.data?.let { data -> _loginByOtp.send(data) }
                            it.message?.let { error -> appendErrorLogin(error) }
                            _loginLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorLogin(mContext.getString(R.string.exception_network))
            }
        })
    }

    fun loginOtpCode(otpCodeRequest: ResetPwCodeRequest) {
        networkConnectivity.checkInternetConnection(object : NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .verifyOtp(otpCodeRequest)
                        .onEach {
                            it.data?.let { data -> _otpLogin.value = data }
                            it.message?.let { error -> appendErrorOtpVerify(error) }
                            _otpLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorOtpVerify(mContext.getString(R.string.exception_network))
            }
        })
    }

    fun updateFcm(updateFirebaseTokenRequest: UpdateFirebaseTokenRequest, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .updateFCMToken(updateFirebaseTokenRequest, accessToken)
                        .onEach {
                            it.data?.let { data -> _updateFcm.send(data) }
                            it.message?.let { error -> appendErrorUpdateFCM(error) }
                            _updateFcmLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorUpdateFCM(mContext.getString(R.string.exception_network))
            }
        })
    }

    private fun appendErrorLogin(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _loginError.value = newMessage
    }

    fun removeLoginError() {
        val currentMessage = loginError.value
        currentMessage.remove()
        _loginError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorOtpVerify(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _otpError.value = newMessage
    }

    fun removeOtpVerifyError() {
        val currentMessage = otpError.value
        currentMessage.remove()
        _otpError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorUpdateFCM(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _updateFcmError.value = newMessage
    }

    fun removeUpdateFCMError() {
        val currentMessage = updateFcmError.value
        currentMessage.remove()
        _updateFcmError.value = ArrayDeque(emptyList())
    }
}