package com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.ArrayDeque

class BeneficiaryAccountViewModel(
    private val useCases: TransferBankUseCases,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> get() = _stateLoading
    private val _globalBanksSaved = Channel<List<GlobalBankSavedResponse>?>()
    val globalBanksSaved get() = _globalBanksSaved.receiveAsFlow()
    private val _globalBanks = Channel<List<FetchBankByCountryResponse>?>()
    val globalBanks get() = _globalBanks.receiveAsFlow()
    private val _nations = Channel<List<FetchNationResponse>?>()
    val nations get() = _nations.receiveAsFlow()
    private val _relationships = Channel<List<FetchRelationshipsResponse>?>()
    val relationships get() = _relationships.receiveAsFlow()

    private val _savingGlobalBankLoading = MutableStateFlow(false)
    val savingGlobalBankLoading: StateFlow<Boolean> get() = _savingGlobalBankLoading
    private val _savingGlobalBank = Channel<BankStateResponse?>()
    val savingGlobalBank get() = _savingGlobalBank.receiveAsFlow()

    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> get() = _stateError

    fun onTriggerEvent(event: BeneficiaryAccountEvents) {
        when(event) {
            is BeneficiaryAccountEvents.GetGlobalBanksNationsAndRelations ->
                getGlobalBanksNationsAndRelationships(event.countryCode)
            is BeneficiaryAccountEvents.SavingGlobalBank ->
                savingGlobalBank(
                    bankCode = event.bankCode, bankAccNum = event.bankAccNum, bankAccName = event.bankAccName,
                    relationCode = event.relationCode, nationCode = event.nationCode, addressStreet = event.addressStreet,
                    addressCity = event.addressCity
                )
            is BeneficiaryAccountEvents.RemoveHeadMessageError -> removeHeadMessageError()
        }
    }

    private fun getGlobalBanksNationsAndRelationships(countryCode: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getBanksNationsAndRelationships(countryCode = countryCode)
                        .onEach {
                            it.data?.let { data ->
                                _globalBanksSaved.send(data.banksSaved)
                                _globalBanks.send(data.banks)
                                _nations.send(data.nations)
                                _relationships.send(data.relationship)
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

    private fun savingGlobalBank(
        bankCode: String, bankAccNum: String, bankAccName: String,
        relationCode: String, nationCode: String, addressStreet: String,
        addressCity: String
    ) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .savingGlobalBank(
                            bankCode = bankCode, accNum = bankAccNum, accName = bankAccName,
                            relationCode = relationCode, nationCode = nationCode, address = addressStreet,
                            city = addressCity
                        ).onEach {
                            it.data?.let { data -> _savingGlobalBank.send(data) }
                            it.message?.let { error -> appendMessageError(error) }
                            _savingGlobalBankLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendMessageError(Constants.networkError)
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
}
