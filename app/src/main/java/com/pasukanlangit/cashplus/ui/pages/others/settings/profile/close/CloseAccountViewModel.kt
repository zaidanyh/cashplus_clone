package com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class CloseAccountViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity
): ViewModel() {

    private val _deleteAccountLoading = MutableStateFlow(false)
    val deleteAccountLoading: StateFlow<Boolean> get() = _deleteAccountLoading
    private val _deleteAccount = Channel<Boolean?>()
    val deleteAccount get() = _deleteAccount.receiveAsFlow()
    private val _deleteAccountError = MutableStateFlow(ArrayDeque<String>())
    val deleteAccountError: StateFlow<Queue<String>> = _deleteAccountError

    fun deleteAccount(uuid: String, reason: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .deleteAccount(uuid = uuid, reason = reason, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _deleteAccount.send(data) }
                            it.message?.let { error -> appendMessageError(error) }
                            _deleteAccountLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendMessageError(Constants.networkError)
            }
        })
    }

    private fun appendMessageError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _deleteAccountError.value = newMessage
    }

    fun removeMessageError() {
        val currentMessage = deleteAccountError.value
        currentMessage.remove()
        _deleteAccountError.value = ArrayDeque(emptyList())
    }
}