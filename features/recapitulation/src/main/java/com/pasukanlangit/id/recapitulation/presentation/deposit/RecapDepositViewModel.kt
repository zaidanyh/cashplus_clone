package com.pasukanlangit.id.recapitulation.presentation.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.usecase.RecapitulationUseCase
import com.pasukanlangit.id.recapitulation.domain.utils.RecapDepositResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class RecapDepositViewModel(
    private val stateNetwork: NetworkConnectivity,
    private val useCase: RecapitulationUseCase
): ViewModel() {
    private val _summaryMutation = Channel<PagingData<MutationDepositResponse>>()
    val summaryMutation get() = _summaryMutation.receiveAsFlow()

    private val _mutationLoading = MutableStateFlow(false)
    val mutationLoading: StateFlow<Boolean> get() = _mutationLoading
    private val _mutationBalance = Channel<RecapDepositResponse?>()
    val mutationBalance get() = _mutationBalance.receiveAsFlow()
    private val _mutationError = MutableStateFlow(ArrayDeque<String>())
    val mutationError: StateFlow<Queue<String>> get() = _mutationError

    fun getSummaryMutation(dateStart: String, dateEnd: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase.summaryMutationDeposit(dateStart, dateEnd).onEach { response ->
                        _summaryMutation.send(response)
                    }.launchIn(viewModelScope)
                    useCase.mutationDeposit(
                        dateStart = dateStart, dateEnd = dateEnd
                    ).onEach {
                        it.data?.let { data -> _mutationBalance.send(data) }
                        it.message?.let { error -> appendMutationMessageError(error) }
                        _mutationLoading.value = it.isLoading
                    }.launchIn(viewModelScope)
                    return
                }
                appendMutationMessageError(Constants.networkError)
            }
        })
    }

    private fun appendMutationMessageError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _mutationError.value = newMessage
    }
    fun removeMutationMessageError() {
        val currentMessage = mutationError.value
        currentMessage.remove()
        _mutationError.value = ArrayDeque(emptyList())
    }
}