package com.pasukanlangit.id.nobu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.domain.model.AccountBindingResponse
import com.pasukanlangit.id.nobu.domain.model.IsBindedResponse
import com.pasukanlangit.id.nobu.domain.model.UnbindResponse
import com.pasukanlangit.id.nobu.domain.model.VerifyOtpResponse
import com.pasukanlangit.id.nobu.domain.usecase.NobuUseCases
import kotlinx.coroutines.flow.*
import java.util.*

class StateViewModel(
    private val useCases: NobuUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateIsBinded = MutableStateFlow<IsBindedResponse?>(null)
    val stateIsBinded: StateFlow<IsBindedResponse?> = _stateIsBinded

    private val _stateIsUnbind = MutableStateFlow<UnbindResponse?>(null)
    val stateIsUnbind: StateFlow<UnbindResponse?> = _stateIsUnbind

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile: StateFlow<ProfileResponse?> = _profile

    private val _accountBinding = MutableStateFlow<AccountBindingResponse?>(null)
    val accountBinding: StateFlow<AccountBindingResponse?> = _accountBinding

    private val _stateLoadingBinding = MutableStateFlow<Boolean?>(null)
    val stateLoadingBinding: StateFlow<Boolean?> = _stateLoadingBinding

    private val _verifyOtp = MutableStateFlow<VerifyOtpResponse?>(null)
    val verifyOtp: StateFlow<VerifyOtpResponse?> = _verifyOtp

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String?> = _phone
    private val _email = MutableStateFlow("")
    val email: StateFlow<String?> = _email
    private val _name = MutableStateFlow("")
    val name: StateFlow<String?> = _name

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateBindingError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateBindingError: StateFlow<Queue<String>> = _stateBindingError

    fun onTriggerEvent(event: StateEvent) {
        when(event) {
            is StateEvent.RemoveHeadMessage -> removeHeadQueue()
            is StateEvent.CheckBinding -> checkIsBinded()
            is StateEvent.UnBindAccount -> unBindAccount()
            is StateEvent.FetchProfile -> getProfile()
            is StateEvent.SetDataBinding -> {
                _phone.value = event.phone
                _email.value = event.email
                _name.value = event.name
            }
            is StateEvent.VerifyOtp -> otpVerify(event.otpCode)
            is StateEvent.AccountBinding -> {
                accountBinding(event.phone, event.email, event.name)
            }
            is StateEvent.RemoveBindingHeadMessage -> removeBindingHeadQueue()
        }
    }

    private fun checkIsBinded() {
        _stateIsBinded.value = null
        useCases
            .getIsBindedUseCase()
            .onEach {
                it.data?.let { data ->
                    _stateIsBinded.value = data
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun unBindAccount() {
        _stateIsUnbind.value = null
        useCases
            .unBindAccount()
            .onEach {
                it.data?.let { data ->
                    _stateIsUnbind.value = data
                }
                it.message?.let { error ->
                    appendBindingErrorMessage(error)
                }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun getProfile() {
        _profile.value = null
        useCases
            .getProfile()
            .onEach {
                it.data?.let { data ->
                    _profile.value = data
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun accountBinding(phone: String, email: String, name: String) {
        _accountBinding.value = null
        useCases
            .nobuAccountBinding(
                phone, email, name
            )
            .onEach {
                it.data?.let { data ->
                    _accountBinding.value = data
                }
                it.message?.let { error ->
                    appendBindingErrorMessage(error)
                }
                _stateLoadingBinding.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun otpVerify(otpCode: String) {
        _verifyOtp.value = null
        useCases
            .verifyOtpUseCase(otpCode)
            .onEach {
                it.data?.let { data ->
                    _verifyOtp.value = data
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
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

    private fun removeBindingHeadQueue() {
        val currentMessage = stateBindingError.value
        currentMessage.remove()
        _stateBindingError.value = ArrayDeque(currentMessage)
    }

    private fun appendBindingErrorMessage(error: String) {
        val currentMessage = stateBindingError.value
        currentMessage.add(error)
        _stateBindingError.value = ArrayDeque(currentMessage)
    }
}