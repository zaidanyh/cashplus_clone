package com.pasukanlangit.id.ui_cashgold_withdraw.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.model.WithDrawProductResponse
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class CashGoldWDViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _stateGoldBalance = MutableStateFlow<String?>(null)
    val stateGoldBalance: StateFlow<String?> = _stateGoldBalance

    private val _stateGoldBalanceRaw = MutableStateFlow<Double?>(null)
    val stateGoldBalanceRaw: StateFlow<Double?> = _stateGoldBalanceRaw

    private val _stateUserBalance = MutableStateFlow<String?>(null)
    val stateUserBalance: StateFlow<String?> = _stateUserBalance

    private val _stateUserBalanceRaw = MutableStateFlow<Double?>(null)
    val stateUserBalanceRaw: StateFlow<Double?> = _stateUserBalanceRaw

    private val _withDrawResponse = MutableStateFlow<WithDrawProductResponse?>(null)
    val withDrawResponse: StateFlow<WithDrawProductResponse?> = _withDrawResponse

    private val _kycStatus = MutableStateFlow<KycStatus?>(null)
    val kycStatus: StateFlow<KycStatus?> = _kycStatus

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    fun onTriggerEvent(event: CashGoldWDEvent){
        when(event){
            is CashGoldWDEvent.GetGoldBalance -> {
                getGoldBalance()
            }
            is CashGoldWDEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is CashGoldWDEvent.GetSt24Balance -> {
                getSt24Balance()
            }
            is CashGoldWDEvent.GetWithDrawProduct -> {
                getWithDrawProduct()
            }
            is CashGoldWDEvent.CheckKyc -> {
                checkKyc()
            }
        }
    }

    private fun checkKyc() {
        _kycStatus.value = null
        useCases
            .kycStatus()
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { kycStatus ->
//                    _stateNeedKyc.value = kycStatus.isIdentityCardUploaded && kycStatus.isIdentityWithSelfieUploaded && kycStatus.isTaxIdUploaded && !kycStatus.isRejected
                    _kycStatus.value = kycStatus
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getWithDrawProduct() {
        useCases
            .getProductWithDraw()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { data ->
                    _withDrawResponse.value = data
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getSt24Balance() {
        useCases
            .getSt24Profile()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { userBalance ->
                    _stateUserBalance.value = userBalance.balanceCurrency
                    _stateUserBalanceRaw.value = userBalance.balanceRaw
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }


    private fun getGoldBalance(){
        useCases
            .getIndoGoldBalance()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { userBalance ->
                    _stateGoldBalance.value = userBalance.gold
                    _stateGoldBalanceRaw.value = userBalance.goldRaw
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
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