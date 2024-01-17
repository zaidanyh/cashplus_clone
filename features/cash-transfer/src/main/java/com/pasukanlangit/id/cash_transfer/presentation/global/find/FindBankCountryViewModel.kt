package com.pasukanlangit.id.cash_transfer.presentation.global.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryResponse
import com.pasukanlangit.id.cash_transfer.domain.usecase.TransferBankUseCases
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FindBankCountryViewModel(
    private val useCases: TransferBankUseCases,
    private val stateNetwork: NetworkConnectivity
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _countries = Channel<List<FetchCountryResponse>?>()
    val countries = _countries.receiveAsFlow()

    fun onTriggerEvents(events: FindBankCountryEvents) {
        when(events) {
            is FindBankCountryEvents.GetCountries -> getCountries()
            is FindBankCountryEvents.RemoveHeadMessageError -> removeHeadMessageError()
        }
    }

    private fun getCountries() {
        stateNetwork.checkInternetConnection(object : NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getCountryList()
                        .onEach {
                            it.data?.let { data -> _countries.send(data) }
                            it.message?.let { error -> appendMessageError(error) }
                            _stateLoading.value = it.isLoading
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