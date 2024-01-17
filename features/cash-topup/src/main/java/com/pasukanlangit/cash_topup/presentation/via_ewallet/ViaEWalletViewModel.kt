package com.pasukanlangit.cash_topup.presentation.via_ewallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.CostNicePayResponse
import com.pasukanlangit.cash_topup.domain.model.TopUpViaEWalletResponse
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class ViaEWalletViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _amountTopUp = MutableStateFlow<String?>(null)
    val amountTopUp: StateFlow<String?> = _amountTopUp
    private val _adminCost = MutableStateFlow<String?>(null)
    val adminCost: StateFlow<String?> = _adminCost
    private val _billingPhone = MutableStateFlow<String?>(null)
    val billingPhone: StateFlow<String?> = _billingPhone

    private val _getCostNicePayLoading = MutableStateFlow(false)
    val getCostNicePayLoading: StateFlow<Boolean> = _getCostNicePayLoading
    private val _getCostNicePay = Channel<CostNicePayResponse?>()
    val getCostNicePay = _getCostNicePay.receiveAsFlow()
    private val _getCostNicePayError = MutableStateFlow(ArrayDeque<String>())
    val getCostNicePayError: StateFlow<Queue<String>> = _getCostNicePayError

    private val _viaEWalletLoading = MutableStateFlow(false)
    val viaEWalletLoading: StateFlow<Boolean> = _viaEWalletLoading
    private val _viaEWallet = Channel<TopUpViaEWalletResponse?>()
    val viaEWallet = _viaEWallet.receiveAsFlow()
    private val _viaEWalletError = MutableStateFlow(ArrayDeque<String>())
    val viaEWalletError: StateFlow<Queue<String>> = _viaEWalletError

    val isReadyPay: Flow<Boolean> = combine(amountTopUp, adminCost, billingPhone) { amount, admin, billing ->
        !amount.isNullOrEmpty() && !admin.isNullOrEmpty() && !billing.isNullOrEmpty()
    }

    fun onTriggerEvent(event: ViaEWalletEvent) {
        when(event) {
            is ViaEWalletEvent.SetAmountAndAdminCost ->
                setAmountAndAdminCost(
                    amount = event.amount, adminCost = event.adminCost,
                    billingPhone = event.billingPhone
                )
            is ViaEWalletEvent.GetCostNicePay ->
                getCostNicePay(
                    bankMitraCode = event.bankMitraCode, payMethod = event.payMethod,
                    amount = event.amount
                )
            is ViaEWalletEvent.TopUpViaEWallet ->
                viaEWallet(
                    bankMitraCode = event.bankMitraCode, payMethod = event.payMethod,
                    amount = event.amount, billingPhone = event.billingPhone
                )
            is ViaEWalletEvent.RemoveGetCostNicePayError -> removeGetCostError()
            is ViaEWalletEvent.RemoveTopUpViaEWalletError -> removeEWalletError()
        }
    }

    private fun setAmountAndAdminCost(amount: String, adminCost: String, billingPhone: String) {
        _amountTopUp.value = amount
        _adminCost.value = adminCost
        _billingPhone.value = billingPhone
    }

    private fun getCostNicePay(bankMitraCode: String, payMethod: String, amount: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .costNicePay(bankMitraCode = bankMitraCode, pay_method = payMethod, amount = amount)
                        .onEach {
                            it.data?.let { data -> _getCostNicePay.send(data) }
                            it.message?.let { error -> appendGetCostError(error) }
                            _getCostNicePayLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendGetCostError(Constants.networkError)
            }
        })
    }

    private fun viaEWallet(bankMitraCode: String, payMethod: String, amount: String, billingPhone: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .viaEWallet(
                            bankMitraCode = bankMitraCode, payMethodCode = payMethod,
                            amt = amount, billingPhone = billingPhone
                        ).onEach {
                            it.data?.let { data -> _viaEWallet.send(data) }
                            it.message?.let { error -> appendViaEWalletError(error) }
                            _viaEWalletLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendViaEWalletError(Constants.networkError)
            }
        })
    }

    private fun appendGetCostError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _getCostNicePayError.value = newMessage
    }
    private fun removeGetCostError() {
        val currentMessage = getCostNicePayError.value
        currentMessage.remove()
        _getCostNicePayError.value = ArrayDeque(emptyList())
    }

    private fun appendViaEWalletError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _viaEWalletError.value = newMessage
    }
    private fun removeEWalletError() {
        val currentMessage = viaEWalletError.value
        currentMessage.remove()
        _viaEWalletError.value = ArrayDeque(emptyList())
    }
}