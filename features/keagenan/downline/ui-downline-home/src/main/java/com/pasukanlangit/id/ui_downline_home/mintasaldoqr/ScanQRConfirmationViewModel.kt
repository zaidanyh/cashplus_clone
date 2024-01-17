package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.domain_downline.model.MessageResponse
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import kotlinx.coroutines.flow.*
import java.util.*

class ScanQRConfirmationViewModel(
    private val useCases: DownLineUseCases
): ViewModel(){
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _response = MutableStateFlow<MessageResponse?>(null)
    val response: StateFlow<MessageResponse?> = _response

    private val _idQr = MutableStateFlow<String?>(null)

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    fun onTriggerEvent(event: ScanQrConfirmationEvent){
        when(event){
            is ScanQrConfirmationEvent.OnSubmitPin -> {
                submitQRId(pin = event.pin)
            }
            is ScanQrConfirmationEvent.RemoveHeadMessage -> { removeHeadQueue() }
            is ScanQrConfirmationEvent.SetIdQr -> {
                _idQr.value = event.id
            }
        }
    }

    private fun submitQRId(pin: String) {
        useCases
            .scanQRAgent(
                id = _idQr.value,
                pin = pin
            )
            .onEach {
                it.data?.let { resp ->
                    _response.value = resp
                    _response.value = null
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoading.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }


    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }


    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }
}