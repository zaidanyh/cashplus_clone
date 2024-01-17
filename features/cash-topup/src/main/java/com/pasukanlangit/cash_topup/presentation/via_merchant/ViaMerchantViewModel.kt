package com.pasukanlangit.cash_topup.presentation.via_merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.TopUpViaVAResult
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class ViaMerchantViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _viaMerchantLoading = MutableStateFlow(false)
    val viaMerchantLoading: StateFlow<Boolean> = _viaMerchantLoading
    private val _viaMerchant = Channel<TopUpViaVAResult?>()
    val viaMerchant = _viaMerchant.receiveAsFlow()
    private val _viaMerchantError = MutableStateFlow(ArrayDeque<String>())
    val viaMerchantError: StateFlow<Queue<String>> = _viaMerchantError

    fun onTriggerEvent(event: ViaMerchantEvent) {
        when(event) {
            is ViaMerchantEvent.TopUpViaMerchant ->
                viaMerchant(bankMitraCode = event.bankMitraCode, payMethod = event.payMethod, amount = event.amount)
            is ViaMerchantEvent.RemoveViaMerchantError -> removeViaMerchantError()
        }
    }

    private fun viaMerchant(bankMitraCode: String, payMethod: String, amount: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .viaMerchant(
                            bankMitraCode = bankMitraCode, payMethodCode = payMethod,
                            amt = amount
                        ).onEach {
                            it.data?.let { data -> _viaMerchant.send(data) }
                            it.message?.let { error -> appendViaMerchantError(error) }
                            _viaMerchantLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendViaMerchantError(Constants.networkError)
            }
        })
    }

    private fun appendViaMerchantError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _viaMerchantError.value = newMessage
    }

    private fun removeViaMerchantError() {
        val currentMessage = viaMerchantError.value
        currentMessage.remove()
        _viaMerchantError.value = ArrayDeque(emptyList())
    }
}