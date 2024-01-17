package com.pasukanlangit.id.cash_transfer.presentation.global

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.BankStateResponse
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class GlobalBankViewModel(
    private val useCases: TransferBankUseCases,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _globalBankLoading = MutableStateFlow(false)
    val globalBankLoading: StateFlow<Boolean> get() = _globalBankLoading
    private val _globalBank = Channel<List<GlobalBankSavedResponse>?>()
    val globalBank get() = _globalBank.receiveAsFlow()
    private val _globalBankError = MutableStateFlow(ArrayDeque<String>())
    val globalBankError: StateFlow<Queue<String>> get() = _globalBankError

    private val _deleteGlobalBankLoading = MutableStateFlow(false)
    val deleteGlobalBankLoading: StateFlow<Boolean> get() = _deleteGlobalBankLoading
    private val _deleteGlobalBank = Channel<BankStateResponse?>()
    val deleteGlobalBank get() = _deleteGlobalBank.receiveAsFlow()
    private val _deleteGlobalBankError = MutableStateFlow(ArrayDeque<String>())
    val deleteGlobalBankError: StateFlow<Queue<String>> get() = _deleteGlobalBankError

    fun onTriggerEvent(event: GlobalBankEvent) {
        when(event) {
            is GlobalBankEvent.GlobalBankSaved -> globalBankSaved()
            is GlobalBankEvent.RemoveGlobalBankError -> removeGlobalBankError()
            is GlobalBankEvent.DeleteGlobalBankSaved ->
                deleteGlobalBankSaved(bankCode = event.bankCode, bankAccNum = event.bankAccNum)
            is GlobalBankEvent.RemoveDeleteGlobalBankSaved -> removeDeleteGlobalBankError()
        }
    }

    private fun globalBankSaved() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .globalBankSaved()
                        .onEach {
                            it.data?.let { data -> _globalBank.send(data) }
                            it.message?.let { error -> appendGlobalBankError(error) }
                            _globalBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendGlobalBankError(Constants.networkError)
            }
        })
    }

    private fun deleteGlobalBankSaved(bankCode: String, bankAccNum: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .deleteBankUseCase(
                            bank_code = bankCode, bank_acc_num = bankAccNum,
                            isGlobalBank = true
                        )
                        .onEach {
                            it.data?.let { data -> _deleteGlobalBank.send(data) }
                            it.message?.let { error -> appendDeleteGlobalBank(error) }
                            _deleteGlobalBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendDeleteGlobalBank(Constants.networkError)
            }
        })
    }

    private fun appendGlobalBankError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _globalBankError.value = newMessage
    }
    private fun removeGlobalBankError() {
        val currentMessage = globalBankError.value
        currentMessage.remove()
        _globalBankError.value = ArrayDeque(emptyList())
    }

    private fun appendDeleteGlobalBank(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _deleteGlobalBankError.value = newMessage
    }
    private fun removeDeleteGlobalBankError() {
        val currentMessage = deleteGlobalBankError.value
        currentMessage.remove()
        _deleteGlobalBankError.value = ArrayDeque(emptyList())
    }
}