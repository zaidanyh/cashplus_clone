package com.pasukanlangit.cash_topup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cash_topup.domain.model.BalanceResponse
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptListResponse
import com.pasukanlangit.cash_topup.domain.usecase.TopUpUseCase
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class NewTopUpViewModel(
    private val useCase: TopUpUseCase,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _balanceLoading = MutableStateFlow(false)
    val balanceLoading: StateFlow<Boolean> = _balanceLoading
    private val _balance = MutableStateFlow<BalanceResponse?>(null)
    val balance: StateFlow<BalanceResponse?> = _balance
    private val _balanceError = MutableStateFlow(ArrayDeque<String>())
    val balanceError: StateFlow<Queue<String>> = _balanceError

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _flipVA = Channel<List<FlipAcceptListResponse>?>()
    val flipVA = _flipVA.receiveAsFlow()
    private val _flipEWallet = Channel<List<FlipAcceptListResponse>?>()
    val flipEWallet = _flipEWallet.receiveAsFlow()
    private val _flipOther = Channel<List<FlipAcceptListResponse>?>()
    val flipOther = _flipOther.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    fun onTriggerEvent(event: InitialEvent) {
        when(event) {
            is InitialEvent.GetBalance -> getBalance()
            is InitialEvent.RemoveBalanceError -> removeBalanceErrorMessage()
            is InitialEvent.GetFlipAcceptList -> getFlipAcceptList()
            is InitialEvent.RemoveHeadMessage -> removeHeadMessage()
        }
    }

    private fun getBalance() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .balanceCheck()
                        .onEach {
                            it.data?.let { data -> _balance.value = data }
                            it.message?.let { error -> appendBalanceError(error) }
                            _balanceLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendBalanceError(Constants.networkError)
            }
        })
    }

    private fun getFlipAcceptList() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .flipAcceptList()
                        .onEach {
                            it.data?.let { data ->
                                _flipVA.send(data.filter { item -> item.priority.toInt() == 1 })
                                _flipEWallet.send(data.filter { item -> item.priority.toInt() == 2 })
                                _flipOther.send(data.filter { item -> item.priority.toInt() == 3 })
                            }
                            it.message?.let { error -> appendMessageError(error) }
                            _stateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendMessageError(Constants.networkError)
            }
        })
    }

    private fun appendBalanceError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _balanceError.value = newMessage
    }

    private fun removeBalanceErrorMessage() {
        val currentMessage = balanceError.value
        currentMessage.remove()
        _balanceError.value = ArrayDeque(emptyList())
    }

    private fun appendMessageError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }

    private fun removeHeadMessage() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }
}