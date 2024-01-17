package com.pasukanlangit.cash_topup.presentation.via_bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.TopUpViaBankResponse
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class ViaBankViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _viaBankLoading = MutableStateFlow(false)
    val viaBankLoading: StateFlow<Boolean> = _viaBankLoading
    private val _viaBank = Channel<TopUpViaBankResponse?>()
    val viaBank = _viaBank.receiveAsFlow()
    private val _viaBankError = MutableStateFlow(ArrayDeque<String>())
    val viaBankError: StateFlow<Queue<String>> = _viaBankError

    fun onTriggerEvent(event: ViaBankEvent) {
        when(event) {
            is ViaBankEvent.TopUpViaBank -> topUpViaBank(event.bank, event.value)
            is ViaBankEvent.RemoveViaBankError -> removeViaBankError()
        }
    }

    private fun topUpViaBank(bank: String, value: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .viaBank(bankName = bank, value = value)
                        .onEach {
                            it.data?.let { data -> _viaBank.send(data) }
                            it.message?.let { error -> appendViaBankError(error) }
                            _viaBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendViaBankError(Constants.networkError)
            }
        })
    }

    private fun appendViaBankError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _viaBankError.value = newMessage
    }

    private fun removeViaBankError() {
        val currentMessage = _viaBankError.value
        currentMessage.remove()
        _viaBankError.value = ArrayDeque(emptyList())
    }
}