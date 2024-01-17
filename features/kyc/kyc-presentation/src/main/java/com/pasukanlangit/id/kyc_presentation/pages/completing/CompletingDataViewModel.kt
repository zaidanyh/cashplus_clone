package com.pasukanlangit.id.kyc_presentation.pages.completing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.kyc_domain.domain.model.EKycProfileUser
import com.pasukanlangit.id.kyc_domain.domain.temp.UploadAndVerify
import com.pasukanlangit.id.kyc_domain.domain.temp.VerifyOnly
import com.pasukanlangit.id.kyc_domain.domain.usecase.KycUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class CompletingDataViewModel(
    private val useCases: KycUseCase
): ViewModel() {

    private val _stateFirst = MutableStateFlow(false)
    val stateFirst: StateFlow<Boolean> = _stateFirst
    private val _stateSecond = MutableStateFlow(false)
    val stateSecond: StateFlow<Boolean> = _stateSecond

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError
    private val _profile = MutableStateFlow<EKycProfileUser?>(null)
    val profile: StateFlow<EKycProfileUser?> = _profile

    private val _uploadLoading = MutableStateFlow(false)
    val uploadLoading: StateFlow<Boolean> = _uploadLoading
    private val _uploadError = MutableStateFlow(ArrayDeque<String>())
    val uploadError: StateFlow<Queue<String>> = _uploadError
    private val _eKycUploadAndVerify = Channel<UploadAndVerify?>()
    val eKycUploadAndVerify = _eKycUploadAndVerify.receiveAsFlow()

    private val _verifyLoading = MutableStateFlow(false)
    val verifyLoading: StateFlow<Boolean> = _verifyLoading
    private val _verifyError = MutableStateFlow(ArrayDeque<String>())
    val verifyError: StateFlow<Queue<String>> = _verifyError
    private val _eKycOnlyVerify = Channel<VerifyOnly?>()
    val eKycOnlyVerify = _eKycOnlyVerify.receiveAsFlow()

    fun onTriggerEvents(event: CompletingEvents) {
        when(event) {
            is CompletingEvents.SetStateFirst -> setStateFirst(event.state)
            is CompletingEvents.SetStateSecond -> setStateSecond(event.state)
            is CompletingEvents.EKycProfileUser -> eKycProfileUser()
            is CompletingEvents.RemoveHeadProfileMessage -> removeHeadProfileQueue()
            is CompletingEvents.EKycUploadVerify ->
                eKycUploadAndVerify(event.verificationType, event.uploadType, event.base64Data)
            is CompletingEvents.RemoveHeadUploadMessage -> removeHeadUploadQueue()
            is CompletingEvents.EKycOnlyVerify -> eKycOnlyVerify(event.verificationType)
            is CompletingEvents.RemoveHeadVerifyMessage -> removeHeadVerifyQueue()
        }
    }

    private fun setStateFirst(value: Boolean) {
        _stateFirst.value = value
    }

    private fun setStateSecond(value: Boolean) {
        _stateSecond.value = value
    }

    private fun eKycProfileUser() {
        useCases
            .eKycProfile()
            .onEach {
                it.data?.let { data -> _profile.value = data }
                it.message?.let { error -> appendErrorProfileMessage(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun eKycUploadAndVerify(
        verificationType: String,
        uploadType: String,
        base64: String
    ) {
        useCases
            .eKycUploadVerify(
                verificationType = verificationType,
                uploadType = uploadType, base64Data = base64
            ).onEach {
                it.data?.let { data -> _eKycUploadAndVerify.send(data) }
                it.message?.let { error -> appendErrorUploadMessage(error) }
                _uploadLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun eKycOnlyVerify(verificationType: String) {
        useCases
            .eKycOnlyVerify(verificationType = verificationType)
            .onEach {
                it.data?.let { data -> _eKycOnlyVerify.send(data) }
                it.message?.let { error -> appendErrorVerifyMessage(error) }
                _verifyLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun appendErrorProfileMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = ArrayDeque(newMessage)
    }

    private fun removeHeadProfileQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorUploadMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _uploadError.value = ArrayDeque(newMessage)
    }

    private fun removeHeadUploadQueue() {
        val current = uploadError.value
        current.remove()
        _uploadError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorVerifyMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _verifyError.value = newMessage
    }

    private fun removeHeadVerifyQueue() {
        val currentMessage = verifyError.value
        currentMessage.remove()
        _verifyError.value = ArrayDeque(emptyList())
    }
}