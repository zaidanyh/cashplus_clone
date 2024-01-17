package com.pasukanlangit.id.ui_downline_transfersaldo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.CheckBalanceResponse
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.ui_downline_transfersaldo.paging.DownlinePagingSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class TransferSaldoAgenViewModel(
    private val stateNetwork: NetworkConnectivity,
    private val useCases: DownLineUseCases
): ViewModel() {
    private val _balanceCheckLoading = MutableStateFlow(false)
    val balanceCheckLoading: StateFlow<Boolean> get() = _balanceCheckLoading
    private val _balanceCheck = Channel<CheckBalanceResponse?>()
    val balanceCheck get() = _balanceCheck.receiveAsFlow()
    private val _balanceCheckError = MutableStateFlow(ArrayDeque<String>())
    val balanceCheckError: StateFlow<Queue<String>> get() = _balanceCheckError

    private val _isLoadingButton = MutableStateFlow(false)
    val isLoadingButton: StateFlow<Boolean> get() = _isLoadingButton
    private val _messageSuccess = Channel<String?>()
    val messageSuccess get() = _messageSuccess.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    fun onTriggerEvent(event: TransferBalanceEvent) {
        when(event) {
            is TransferBalanceEvent.TransferBalanceDownLine ->
                transferDownLine(pin = event.pin, dest = event.dest, nominal = event.nominal)
            is TransferBalanceEvent.RemoveHeadQueue -> removeHeadQueue()
            is TransferBalanceEvent.CheckBalance -> balanceCheck()
            is TransferBalanceEvent.RemoveCheckBalanceError -> removeBalanceCheckError()
        }
    }

    fun getDownLine(
        repository: DownLineRepository, dateStart: String,
        dateEnd: String, type: String, value: String
    ): Flow<PagingData<DownLineItem>> = Pager(PagingConfig(pageSize = 1)) {
        DownlinePagingSource(
            repository = repository,
            dateStart = dateStart,
            dateEnd = dateEnd,
            type = type,
            value = value
        )
    }.flow.cachedIn(viewModelScope)

    private fun transferDownLine(pin: String, dest: String?, nominal: String?) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .transferDepositDownLine(
                            pin = pin,
                            dest = dest,
                            nominal = nominal
                        )
                        .onEach {
                            it.data?.let { data -> _messageSuccess.send(data.message) }
                            it.message?.let { error -> appendErrorMessage(error) }
                            _isLoadingButton.value = it.isLoading
                        }
                        .launchIn(viewModelScope)
                    return
                }
                appendErrorMessage(Constants.networkError)
            }
        })
    }

    private fun balanceCheck() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .balanceCheck()
                        .onEach {
                            it.data?.let { data -> _balanceCheck.send(data) }
                            it.message?.let { error -> appendBalanceCheckError(error) }
                            _balanceCheckLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendBalanceCheckError(Constants.networkError)
            }
        })
    }

    private fun removeBalanceCheckError() {
        val currentMessage = balanceCheckError.value
        currentMessage.remove()
        _balanceCheckError.value = ArrayDeque(emptyList())
    }
    private fun appendBalanceCheckError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _balanceCheckError.value = newMessage
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }
    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = ArrayDeque(newMessage)
    }
}