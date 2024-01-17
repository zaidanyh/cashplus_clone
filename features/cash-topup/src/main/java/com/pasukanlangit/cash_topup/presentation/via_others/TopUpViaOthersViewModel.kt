package com.pasukanlangit.cash_topup.presentation.via_others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.CostNicePayResponse
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class TopUpViaOthersViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _getCostNicePayLoading = MutableStateFlow(false)
    val getCostNicePayLoading: StateFlow<Boolean> = _getCostNicePayLoading
    private val _getCostNicePay = Channel<CostNicePayResponse?>()
    val getCostNicePay = _getCostNicePay.receiveAsFlow()
    private val _getCostNicePayError = MutableStateFlow(ArrayDeque<String>())
    val getCostNicePayError: StateFlow<Queue<String>> = _getCostNicePayError

    fun onTriggerEvent(event: ViaOthersEvent) {
        when(event) {
            is ViaOthersEvent.GetCostNicePay ->
                getCostNicePay(bankMitraCode = event.bankMitraCode, payMethod = event.payMethod, amount = event.amount)
            is ViaOthersEvent.RemoveGetCostNicePayError -> removeGetCostError()
        }
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
}