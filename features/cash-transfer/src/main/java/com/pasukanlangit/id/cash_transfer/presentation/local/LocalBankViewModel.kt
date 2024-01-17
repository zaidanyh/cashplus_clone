package com.pasukanlangit.id.cash_transfer.presentation.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.BankStateResponse
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankListResponse
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionResponse
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.cash_transfer.presentation.local.utils.AccountBank
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.ArrayDeque

class LocalBankViewModel(
    private val useCases: TransferBankUseCases,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> get() = _stateLoading
    private val _banksSaved = Channel<List<LocalBankSavedResponse>?>()
    val banksSaved get() = _banksSaved.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> get() = _stateError

    private val _bankListLoading = MutableStateFlow(false)
    val bankListLoading: StateFlow<Boolean> get() = _bankListLoading
    private val _bankList = Channel<List<LocalBankListResponse>?>()
    val bankList get() = _bankList.receiveAsFlow()
    private val _bankListError = MutableStateFlow(ArrayDeque<String>())
    val bankListError: StateFlow<Queue<String>> get() = _bankListError

    private val _account = MutableStateFlow<AccountBank?>(null)
    val account: StateFlow<AccountBank?> get() = _account

    private val _tagBankLoading = MutableStateFlow(false)
    val tagBankLoading: StateFlow<Boolean> get() = _tagBankLoading
    private val _tagBank = Channel<TransferTransactionResponse?>()
    val tagBank get() = _tagBank.receiveAsFlow()
    private val _tagBankError = MutableStateFlow(ArrayDeque<String>())
    val tagBankError: StateFlow<Queue<String>> get() = _tagBankError

    private val _savingBankLoading = MutableStateFlow(false)
    val savingBankLoading: StateFlow<Boolean> get() = _savingBankLoading
    private val _savingBankAcc = Channel<BankStateResponse?>()
    val savingBankAcc get() = _savingBankAcc.receiveAsFlow()
    private val _savingBankError = MutableStateFlow(ArrayDeque<String>())
    val savingBankError: StateFlow<Queue<String>> get() = _savingBankError

    private val _deleteBankLoading = MutableStateFlow(false)
    val deleteBankLoading: StateFlow<Boolean> get() = _deleteBankLoading
    private val _deleteBankAcc = Channel<BankStateResponse?>()
    val deleteBankAcc get() = _deleteBankAcc.receiveAsFlow()
    private val _deleteBankError = MutableStateFlow(ArrayDeque<String>())
    val deleteBankError: StateFlow<Queue<String>> get() = _deleteBankError

    fun onTriggerEvent(event: LocalBankEvent) {
        when(event) {
            is LocalBankEvent.GetBankSaved -> localBankSaved()
            is LocalBankEvent.RemoveBankSavedError -> removeHeadQueue()
            is LocalBankEvent.GetBankList -> localBankList()
            is LocalBankEvent.RemoveBankListError -> removeBankListError()
            is LocalBankEvent.SetAccountBank -> setAccountBank(account = event.account)
            is LocalBankEvent.ResetAccountBank -> removeAccountBankValue()
            is LocalBankEvent.SendTAGBank ->
                sendTagBank(codeProduct = event.codeProduct, dest = event.dest, pin = event.pin)
            is LocalBankEvent.RemoveTAGBankError -> removeTAGBankError()
            is LocalBankEvent.SavingBankAcc ->
                localBankSaving(bankCode = event.bankCode, bankAccNum = event.bankAccNum, bankAccName = event.bankAccName)
            is LocalBankEvent.RemoveSavingBankError -> removeSavingBankError()
            is LocalBankEvent.DeleteBankAcc -> localBankDelete(bankCode = event.bankCode, bankAccNum = event.bankAccNum)
            is LocalBankEvent.RemoveDeleteBankError -> removeDeleteBankError()
        }
    }

    private fun localBankSaved() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .localBankSaved()
                        .onEach {
                            it.message?.let { error -> appendErrorMessage(error) }
                            it.data?.let { data -> _banksSaved.send(data) }
                            _stateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorMessage(Constants.networkError)
            }
        })
    }

    private fun localBankList() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .localBanksList()
                        .onEach {
                            it.message?.let { error -> appendErrorBankList(error) }
                            it.data?.let { data -> _bankList.send(data) }
                            _bankListLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorBankList(Constants.networkError)
            }
        })
    }

    private fun setAccountBank(account: AccountBank?) {
        if (account == null) return
        _account.value = account
    }

    private fun sendTagBank(codeProduct: String, dest: String, pin: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .bankTransferTransaction(
                            codeProduct = codeProduct, dest = dest, pin = pin, reqId = ""
                        ).onEach {
                            it.data?.let { data -> _tagBank.send(data) }
                            it.message?.let { error -> appendErrorTAGBank(error) }
                            _tagBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorTAGBank(Constants.networkError)
            }
        })
    }

    private fun localBankSaving(bankCode: String, bankAccNum: String, bankAccName: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .localSavingBankUseCase(
                            bank_code = bankCode,
                            bank_acc_num = bankAccNum,
                            bank_acc_name = bankAccName
                        ).onEach {
                            it.message?.let { error -> appendErrorSavingBank(error) }
                            it.data?.let { data -> _savingBankAcc.send(data) }
                            _savingBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorSavingBank(Constants.networkError)
            }
        })
    }

    private fun localBankDelete(bankCode: String, bankAccNum: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .deleteBankUseCase(
                            bank_code = bankCode,
                            bank_acc_num = bankAccNum
                        ).onEach {
                            it.message?.let { error -> appendErrorDeleteBank(error) }
                            it.data?.let { data -> _deleteBankAcc.send(data) }
                            _deleteBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorDeleteBank(Constants.networkError)
            }
        })
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }
    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorBankList(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _bankListError.value = newMessage
    }
    private fun removeBankListError() {
        val currentMessage = bankListError.value
        currentMessage.remove()
        _bankListError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorTAGBank(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _tagBankError.value = newMessage
    }
    private fun removeTAGBankError() {
        val currentMessage = tagBankError.value
        currentMessage.remove()
        _tagBankError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorSavingBank(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _savingBankError.value = newMessage
    }
    private fun removeSavingBankError() {
        val currentMessage = savingBankError.value
        currentMessage.remove()
        _savingBankError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorDeleteBank(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _deleteBankError.value = newMessage
    }
    private fun removeDeleteBankError() {
        val currentMessage = deleteBankError.value
        currentMessage.remove()
        _deleteBankError.value = ArrayDeque(emptyList())
    }

    private fun removeAccountBankValue() {
        _account.value = null
    }
}