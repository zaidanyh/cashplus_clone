package com.pasukanlangit.cash_topup.presentation.via_others.qris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.TopUpViaQRISResponse
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class ViaQRISViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _viaQRISLoading = MutableStateFlow(false)
    val viaQRISLoading: StateFlow<Boolean> = _viaQRISLoading
    private val _viaQRIS = Channel<TopUpViaQRISResponse?>()
    val viaQRIS = _viaQRIS.receiveAsFlow()
    private val _viaQRISError = MutableStateFlow(ArrayDeque<String>())
    val viaQRISError: StateFlow<Queue<String>> = _viaQRISError

    fun onTriggerEvent(event: ViaQRISEvent) {
        when(event) {
            is ViaQRISEvent.TopUpViaQRIS -> viaQRIS(event.amount)
            is ViaQRISEvent.RemoveTopUpViaQRISError -> removeViaQRISError()
        }
    }

    private fun viaQRIS(amount: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .viaQRIS(amount = amount)
                        .onEach {
                            it.data?.let { data -> _viaQRIS.send(data) }
                            it.message?.let { error -> appendViaQRISError(error) }
                            _viaQRISLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendViaQRISError(Constants.networkError)
            }
        })
    }

    private fun appendViaQRISError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _viaQRISError.value = newMessage
    }
    private fun removeViaQRISError() {
        val currentMessage = viaQRISError.value
        currentMessage.remove()
        _viaQRISError.value = ArrayDeque(emptyList())
    }
}