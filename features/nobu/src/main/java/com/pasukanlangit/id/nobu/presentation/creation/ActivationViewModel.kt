package com.pasukanlangit.id.nobu.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.domain.model.AccountCreationResponse
import com.pasukanlangit.id.nobu.domain.model.SecurityQuestions
import com.pasukanlangit.id.nobu.domain.model.TermConditionResponse
import com.pasukanlangit.id.nobu.domain.model.VerifyOtpResponse
import com.pasukanlangit.id.nobu.domain.temp.AccountCreation
import com.pasukanlangit.id.nobu.domain.usecase.NobuUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class ActivationViewModel(
    private val useCases: NobuUseCases
): ViewModel() {
    private val _stateFirst = MutableStateFlow(false)
    val stateFirst: StateFlow<Boolean> = _stateFirst

    private val _stateSecond = MutableStateFlow(false)
    val stateSecond: StateFlow<Boolean> = _stateSecond

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile: StateFlow<ProfileResponse?> = _profile

    private val _questions = MutableStateFlow<List<SecurityQuestions>?>(null)
    val questions: StateFlow<List<SecurityQuestions>?> = _questions

    private val _termCondition = MutableStateFlow<TermConditionResponse?>(null)
    val termCondition: StateFlow<TermConditionResponse?> = _termCondition

    private val _accountCreation = MutableStateFlow<AccountCreationResponse?>(null)
    val accountCreation: StateFlow<AccountCreationResponse?> = _accountCreation

    private val _verifyOtp = MutableStateFlow<VerifyOtpResponse?>(null)
    val verifyOtp: StateFlow<VerifyOtpResponse?> = _verifyOtp

    fun setStateFirst(value: Boolean) {
        _stateFirst.value = value
    }

    fun setStateSecond(value: Boolean) {
        _stateSecond.value = value
    }

    fun getQuestionsAndTermConditionAndProfile() {
        _questions.value = null
        useCases
            .getSecurityQuestions()
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { questions ->
                    _questions.value = questions
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)

        _profile.value = null
        useCases
            .getProfile()
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { profile ->
                    _profile.value = profile
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)

        _termCondition.value = null
        useCases
            .getTermCondition()
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { term ->
                    _termCondition.value = term
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)
    }

    fun nobuAccountCreation(accountCreation: AccountCreation) {
        useCases
            .nobuAccountCreation(
                accountCreation.phone,
                accountCreation.email,
                accountCreation.name,
                accountCreation.securityAnswer,
                accountCreation.securityQuestion,
                accountCreation.securityCode
            )
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { response ->
                    _accountCreation.value = response
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)
    }

    fun verifyOtp(otpCode: String) {
        useCases
            .verifyOtpUseCase(otpCode)
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { response ->
                    _verifyOtp.value = response
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)
    }

    fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = ArrayDeque(newMessage)
    }
}