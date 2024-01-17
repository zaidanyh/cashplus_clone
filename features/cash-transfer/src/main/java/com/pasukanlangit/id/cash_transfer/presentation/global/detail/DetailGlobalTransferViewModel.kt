package com.pasukanlangit.id.cash_transfer.presentation.global.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.CheckBalanceResponse
import com.pasukanlangit.id.cash_transfer.domain.model.RateResponse
import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionResponse
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.cash_transfer.domain.utils.GlobalBankCreateTransTAGResponse
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class DetailGlobalTransferViewModel(
    private val useCases: TransferBankUseCases,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _rateConversion = Channel<RateResponse?>()
    val rateConversion = _rateConversion.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _balanceLoading = MutableStateFlow(false)
    val balanceLoading: StateFlow<Boolean> = _balanceLoading
    private val _balance = Channel<CheckBalanceResponse?>()
    val balance = _balance.receiveAsFlow()
    private val _balanceError = MutableStateFlow(ArrayDeque<String>())
    val balanceError: StateFlow<Queue<String>> = _balanceError

    private val _createTransLoading = MutableStateFlow(false)
    val createTransLoading: StateFlow<Boolean> = _createTransLoading
    private val _createTrans = Channel<GlobalBankCreateTransTAGResponse?>()
    val createTrans = _createTrans.receiveAsFlow()
    private val _createTransError = MutableStateFlow(ArrayDeque<String>())
    val createTransError: StateFlow<Queue<String>> = _createTransError

    private val _transferTransLoading = MutableStateFlow(false)
    val transferTransLoading: StateFlow<Boolean> = _transferTransLoading
    private val _transferTrans = Channel<TransferTransactionResponse?>()
    val transferTrans = _transferTrans.receiveAsFlow()
    private val _transferTransError = MutableStateFlow(ArrayDeque<String>())
    val transferTransError: StateFlow<Queue<String>> = _transferTransError

    fun onTriggerEvents(event: DetailGlobalTransferEvents) {
        when(event) {
            is DetailGlobalTransferEvents.GetRateConversion ->
                rateConversion(currency = event.currency, amount = event.amount)
            is DetailGlobalTransferEvents.RemoveHeadMessageError -> removeHeadMessageError()

            is DetailGlobalTransferEvents.CheckBalance -> checkBalance()
            is DetailGlobalTransferEvents.RemoveBalanceError -> removeBalanceError()

            is DetailGlobalTransferEvents.GlobalBankCreateTrans ->
                createTrans(
                    quoteId = event.quoteId, relationCode = event.relationCode, nationCode = event.nationCode,
                    bankCode = event.bankCode, bankAccNum = event.bankAccNum, bankAccName = event.bankAccName,
                    countryCode = event.countryCode, address = event.address, city = event.city, reqId = event.reqId
                )
            is DetailGlobalTransferEvents.RemoveCreateTransError -> removeCreateTransError()

            is DetailGlobalTransferEvents.TransferTransaction ->
                transferTransaction(productCode = event.productCode, dest = event.dest, pin = event.pin, reqId = event.reqId)
            is DetailGlobalTransferEvents.RemoveTransferTransError -> removeTransferTransError()
        }
    }

    private fun rateConversion(currency: String, amount: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getRateConversion(currency = currency, amount = amount)
                        .onEach {
                            it.data?.let { data -> _rateConversion.send(data) }
                            it.message?.let { error -> appendMessageError(error) }
                            _stateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendMessageError(Constants.networkError)
            }
        })
    }

    private fun checkBalance() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .checkBalance()
                        .onEach {
                            it.data?.let { data -> _balance.send(data) }
                            it.message?.let { error -> appendErrorBalance(error) }
                            _balanceLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorBalance(Constants.networkError)
            }
        })
    }

    private fun createTrans(
        quoteId: String, relationCode: String, nationCode: String, bankCode: String, bankAccNum: String,
        bankAccName: String, countryCode: String, address: String, city: String, reqId: String?
    ) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .globalBankCreateTrans(
                            quoteId = quoteId, relationCode = relationCode, nationCode = nationCode,
                            bankCode = bankCode, bankAccNum = bankAccNum, bankAccName = bankAccName,
                            countryCode = countryCode, address = address, city = city, reqId = reqId
                        ).onEach {
                            it.data?.let { data -> _createTrans.send(data) }
                            it.message?.let { error -> appendCreateTransError(error) }
                            _createTransLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendCreateTransError(Constants.networkError)
            }
        })
    }

    private fun transferTransaction(productCode: String, dest: String, pin: String, reqId: String?) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .bankTransferTransaction(codeProduct = productCode, dest = dest, pin = pin, reqId = reqId)
                        .onEach {
                            it.data?.let { data -> _transferTrans.send(data) }
                            it.message?.let { error -> appendTransferTransError(error) }
                            _transferTransLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendTransferTransError(Constants.networkError)
            }
        })
    }

    private fun appendMessageError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }
    private fun removeHeadMessageError() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorBalance(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _balanceError.value = newMessage
    }
    private fun removeBalanceError() {
        val currentMessage = balanceError.value
        currentMessage.remove()
        _balanceError.value = ArrayDeque(emptyList())
    }

    private fun appendCreateTransError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _createTransError.value = newMessage
    }
    private fun removeCreateTransError() {
        val currentMessage = createTransError.value
        currentMessage.remove()
        _createTransError.value = ArrayDeque(emptyList())
    }

    private fun appendTransferTransError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _transferTransError.value = newMessage
    }
    private fun removeTransferTransError() {
        val currentMessage = transferTransError.value
        currentMessage.remove()
        _transferTransError.value = ArrayDeque(emptyList())
    }
}