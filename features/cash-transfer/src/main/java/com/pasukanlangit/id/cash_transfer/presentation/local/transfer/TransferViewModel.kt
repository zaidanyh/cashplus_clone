package com.pasukanlangit.id.cash_transfer.presentation.local.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class TransferViewModel(
    private val useCases: TransferBankUseCases,
    private val networkConnectivity: NetworkConnectivity
): ViewModel() {

    private val _transferLoading = MutableStateFlow(false)
    val transferLoading: StateFlow<Boolean> = _transferLoading
    private val _transfer = Channel<TransferTransactionResponse?>()
    val transfer = _transfer.receiveAsFlow()
    private val _transferError = MutableStateFlow(ArrayDeque<String>())
    val transferError: StateFlow<ArrayDeque<String>> = _transferError

    private val _balanceLoading = MutableStateFlow(false)
    val balanceLoading: StateFlow<Boolean> = _balanceLoading
    private val _balance = Channel<CheckBalanceResponse?>()
    val balance = _balance.receiveAsFlow()
    private val _balanceError = MutableStateFlow(ArrayDeque<String>())
    val balanceError: StateFlow<Queue<String>> = _balanceError

    fun onEventState(event: TransferStateEvent) {
        when(event) {
            is TransferStateEvent.BankTransferTransaction -> bankTransferTransaction(event.codeProduct, event.dest, event.pin, event.reqId)
            is TransferStateEvent.RemoveTransferErrorMessage -> removeMessageTransferError()
            is TransferStateEvent.CheckBalance -> checkBalance()
            is TransferStateEvent.RemoveMessageBalance -> removeMessageBalance()
        }
    }

    private fun checkBalance() {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .checkBalance()
                        .onEach {
                           it.message?.let { error -> appendErrorBalance(error) }
                           it.data?.let { data -> _balance.send(data) }
                           _balanceLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorBalance(Constants.networkError)
            }
        })
    }

    private fun bankTransferTransaction(codeProduct: String, dest: String, pin: String, reqId: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .bankTransferTransaction(codeProduct, dest, pin, reqId)
                        .onEach {
                            it.message?.let { error -> appendErrorTransfer(error) }
                            it.data?.let { data -> _transfer.send(data) }
                            _transferLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorTransfer(Constants.networkError)
            }
        })
    }

    private fun appendErrorTransfer(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _transferError.value = newMessage
    }
    private fun removeMessageTransferError() {
        val currentMessage = transferError.value
        currentMessage.remove()
        _transferError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorBalance(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _balanceError.value = newMessage
    }
    private fun removeMessageBalance() {
        val currentMessage = balanceError.value
        currentMessage.remove()
        _balanceError.value = ArrayDeque(emptyList())
    }
}