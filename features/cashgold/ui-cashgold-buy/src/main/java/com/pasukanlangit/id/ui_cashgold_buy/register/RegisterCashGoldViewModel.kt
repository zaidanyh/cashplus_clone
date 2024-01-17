package com.pasukanlangit.id.ui_cashgold_buy.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.St24Profile
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class RegisterCashGoldViewModel(
    private val cashGoldUseCase: CashGoldUseCase
): ViewModel(){
    private var jobProfile: Job? = null

    private val _isRegistered = MutableStateFlow<Boolean?>(null)

    private val _isUpdated = MutableStateFlow<Boolean?>(null)
    val isUpdated: StateFlow<Boolean?> = _isUpdated

    private val _isLoadingButton = MutableStateFlow(false)
    val isLoadingButton: StateFlow<Boolean> = _isLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _profile = MutableStateFlow<St24Profile?>(null)
    val profile: StateFlow<St24Profile?> = _profile

    init {
        onTriggerEvent(RegisterCashGoldEvent.GetProfile)
    }

    fun onTriggerEvent(event: RegisterCashGoldEvent) {
        when(event){
            is RegisterCashGoldEvent.GetProfile -> {
                getProfile()
            }
            is RegisterCashGoldEvent.RegisterUser -> {
                if(_isRegistered.value == true){
                    updateUser(email = event.email, phone = event.phoneNumber, taxIdentityNumber = event.taxNumber, identityNumber = event.identityNumber)
                }else{
                    register(
                        name = event.name,
                        email = event.email,
                        phoneNumber = event.phoneNumber,
                        taxIdentityNumber = event.taxNumber,
                        identityNumber = event.identityNumber
                    )
                }
            }
            RegisterCashGoldEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
        }
    }

    private fun updateUser(
        taxIdentityNumber: String?,
        phone: String,
        identityNumber: String,
        email: String,
    ) {
        _isUpdated.value = null
        jobProfile?.cancel()
        cashGoldUseCase
            .updateUserCashGold(
                taxIdentityNumber = taxIdentityNumber,
                phone = phone,
                identityNumber = identityNumber,
                email = email,
            )
            .onEach {
                it.data?.let { isUpdated ->
                    _isUpdated.value = isUpdated
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingButton.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    private fun getProfile() {
        _profile.value = null
        jobProfile = cashGoldUseCase
            .getSt24Profile()
            .onEach {
                it.data?.let { profile ->
                    _profile.value = profile
                }
            }
            .launchIn(viewModelScope)
    }

    private fun register(
        name: String,
        email: String,
        phoneNumber: String,
        taxIdentityNumber: String?,
        identityNumber: String
    ){
        _isRegistered.value = null
        jobProfile?.cancel()
        cashGoldUseCase
            .registerUseCase(
                name = name,
                email = email,
                phone = phoneNumber
            )
            .onEach {
                it.data?.let { isRegistered ->
                    _isRegistered.value = isRegistered
                    updateUser(email = email, phone = phoneNumber, taxIdentityNumber = taxIdentityNumber, identityNumber = identityNumber)
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingButton.value = it.isLoading
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