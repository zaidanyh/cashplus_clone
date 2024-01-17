package com.pasukanlangit.id.kyc_presentation.pages.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.kyc_domain.domain.usecase.KycUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class KYCViewModel(
    private val useCases: KycUseCase
): ViewModel() {

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _checkStatus = Channel<EKycStatus>()
    val checkStatus = _checkStatus.receiveAsFlow()

    fun onTriggerEvent(events: EKycEvents) {
        when(events) {
            is EKycEvents.RemoveHeadMessage -> removeHeadQueue()
            is EKycEvents.CheckEKycStatus -> checkEKycStatus()
        }
    }

    private fun checkEKycStatus() {
        useCases
            .eKycStatus()
            .onEach {
                it.data?.let { data ->
                    if (data.isKycApproved) {
                        _checkStatus.send(EKycStatus.Approved)
                        return@onEach
                    }
                    if (data.isKycRejected) {
                        _checkStatus.send(EKycStatus.Rejected(note = data.rejectNote.toString()))
                        return@onEach
                    }
                    if (
                        data.eKycOcrUploaded &&
                        !data.eKycOcrUploadedStatus &&
                        !data.eKycSelfieUploaded &&
                        !data.eKycSelfieUploadedStatus &&
                        !data.isKycApproved &&
                        !data.isKycRejected
                    ) {
                        _checkStatus.send(EKycStatus.IdCardUploaded)
                        return@onEach
                    }
                    if (
                        data.eKycOcrUploaded &&
                        data.eKycOcrUploadedStatus &&
                        !data.eKycSelfieUploaded &&
                        !data.eKycSelfieUploadedStatus &&
                        !data.isKycApproved &&
                        !data.isKycRejected
                    ) {
                        _checkStatus.send(EKycStatus.UploadSelfie)
                        return@onEach
                    }
                    if (
                        data.eKycOcrUploaded &&
                        data.eKycOcrUploadedStatus &&
                        data.eKycSelfieUploaded &&
                        !data.eKycSelfieUploadedStatus &&
                        !data.isKycApproved &&
                        !data.isKycRejected
                    ) {
                        _checkStatus.send(EKycStatus.SelfieUploaded)
                        return@onEach
                    }
                    if (
                        data.eKycOcrUploaded &&
                        data.eKycOcrUploadedStatus &&
                        data.eKycSelfieUploaded &&
                        data.eKycSelfieUploadedStatus &&
                        !data.isKycApproved &&
                        !data.isKycRejected
                    ) {
                        _checkStatus.send(EKycStatus.Waiting)
                        return@onEach
                    }
                    _checkStatus.send(EKycStatus.None)
                }
                it.message?.let { error -> appendErrorMessage(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
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