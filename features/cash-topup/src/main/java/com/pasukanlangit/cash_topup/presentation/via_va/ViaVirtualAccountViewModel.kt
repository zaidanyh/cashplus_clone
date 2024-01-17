package com.pasukanlangit.cash_topup.presentation.via_va

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptCreateBillResponse
import com.pasukanlangit.cash_topup.domain.model.TopUpViaVAResult
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class ViaVirtualAccountViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _viaVALoading = MutableStateFlow(false)
    val viaVALoading: StateFlow<Boolean> get() = _viaVALoading
    private val _viaVaNicePay = Channel<TopUpViaVAResult?>()
    val viaVANicePay get() = _viaVaNicePay.receiveAsFlow()
    private val _viaVAFlipAccept = Channel<FlipAcceptCreateBillResponse?>()
    val viaVAFlipAccept get() = _viaVAFlipAccept.receiveAsFlow()
    private val _viaVAError = MutableStateFlow(ArrayDeque<String>())
    val viaVAError: StateFlow<Queue<String>> get() = _viaVAError

    fun onTriggerEvent(event: ViaVirtualAccountEvent) {
        when(event) {
            is ViaVirtualAccountEvent.TopUpViaVA ->
                viaVirtualAccount(
                    bankCode = event.bankCode, amount = event.amount,
                    paymentMethod = event.paymentMethod, isFlipAccept = event.isFlipAccept
                )
            is ViaVirtualAccountEvent.RemoveViaVAError -> removeViaVAError()
        }
    }

    private fun viaVirtualAccount(bankCode: String, amount: String, paymentMethod: String?, isFlipAccept: Boolean) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    if (isFlipAccept) {
                        useCase
                            .createBillFlip(bankCode = bankCode, amount = amount)
                            .onEach {
                                it.data?.let { data -> _viaVAFlipAccept.send(data) }
                                it.message?.let { error -> appendViaVAError(error) }
                                _viaVALoading.value = it.isLoading
                            }.launchIn(viewModelScope)
                        return
                    }
                    useCase
                        .viaVA(bankMitraCode = bankCode, pay_method = paymentMethod.toString(), amt = amount)
                        .onEach {
                            it.data?.let { data -> _viaVaNicePay.send(data) }
                            it.message?.let { error -> appendViaVAError(error) }
                            _viaVALoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendViaVAError(Constants.networkError)
            }
        })
    }

    private fun appendViaVAError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _viaVAError.value = newMessage
    }

    private fun removeViaVAError() {
        val currentMessage = viaVAError.value
        currentMessage.remove()
        _viaVAError.value = ArrayDeque(emptyList())
    }
}