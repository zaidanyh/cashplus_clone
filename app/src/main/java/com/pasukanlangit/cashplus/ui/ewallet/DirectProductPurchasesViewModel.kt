package com.pasukanlangit.cashplus.ui.ewallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.domain.model.UnitPriceResponse
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class DirectProductPurchasesViewModel(
    private val networkConnectivity: NetworkConnectivity,
    private val useCases: AppUseCases
): ViewModel() {
    private val _balanceLoading = MutableStateFlow(false)
    val balanceLoading: StateFlow<Boolean> = _balanceLoading
    private val _balance = MutableStateFlow<BalanceResponseCore?>(null)
    val balance: StateFlow<BalanceResponseCore?> = _balance
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _calculateLoading = MutableStateFlow(false)
    val calculateLoading: StateFlow<Boolean> = _calculateLoading
    private val _calculate = Channel<UnitPriceResponse?>()
    val calculate = _calculate.receiveAsFlow()
    private val _calculateError = MutableStateFlow(ArrayDeque<String>())
    val calculateError: StateFlow<Queue<String>> = _calculateError

    private val _transactionsLoading = MutableStateFlow(false)
    val transactionsLoading: StateFlow<Boolean> = _transactionsLoading
    private val _transactions = Channel<TransactionTAGResponse?>()
    val transactions = _transactions.receiveAsFlow()
    private val _transactionsError = MutableStateFlow(ArrayDeque<String>())
    val transactionsError: StateFlow<Queue<String>> = _transactionsError

    fun getBalance(uuid: String?, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getBalance(uuid, accessToken)
                        .onEach {
                            it.data?.let { data -> _balance.value = data }
                            it.message?.let { error -> appendHeadError(error) }
                            _balanceLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendHeadError(Constants.networkError)
            }
        })
    }

    fun calculateUnitPrice(uuid: String?, productCode: String, qty: String, dest: String, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .calculateUnitPrice(
                            uuid = uuid, productCode = productCode, qty = qty,
                            dest = dest, accessToken = accessToken
                        ).onEach {
                            it.data?.let { data -> _calculate.send(data) }
                            it.message?.let { error -> appendCalculateError(error) }
                            _calculateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendCalculateError(Constants.networkError)
            }
        })
    }

    fun transactionsRequest(
        uuid: String?, productCode: String,
        dest: String, pin: String, qty: String, reqId: String,
        accessToken: String?
    ) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .billPayTransaction(
                            uuid = uuid, codeProduct = productCode,
                            destination = dest, pin = pin, accessToken = accessToken,
                            qty = qty, reqId = reqId
                        ).onEach {
                            it.data?.let { data -> _transactions.send(data) }
                            it.message?.let { error -> appendTransactionsError(error) }
                            _transactionsLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendTransactionsError(Constants.networkError)
            }
        })
    }

    fun removeHeadMessage() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    fun removeCalculateError() {
        val currentMessage = calculateError.value
        currentMessage.remove()
        _calculateError.value = ArrayDeque(emptyList())
    }

    fun removeHeadTransactionsError() {
        val currentMessage = transactionsError.value
        currentMessage.remove()
        _transactionsError.value = ArrayDeque(emptyList())
    }

    private fun appendHeadError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }

    private fun appendCalculateError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _calculateError.value = newMessage
    }

    private fun appendTransactionsError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _transactionsError.value = newMessage
    }
}