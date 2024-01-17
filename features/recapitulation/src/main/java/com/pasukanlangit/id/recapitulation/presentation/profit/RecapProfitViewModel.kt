package com.pasukanlangit.id.recapitulation.presentation.profit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.recapitulation.domain.usecase.RecapitulationUseCase
import com.pasukanlangit.id.recapitulation.domain.utils.RecapTransResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class RecapProfitViewModel(
    private val stateNetwork: NetworkConnectivity,
    private val useCases: RecapitulationUseCase
): ViewModel() {

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> get() = _stateLoading
    private val _allRecapTrans = Channel<RecapTransResponse?>()
    val allRecapTrans get() = _allRecapTrans.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> get() = _stateError

    private val _keywordInput = Channel<String?>()
    val keywordInput get() = _keywordInput.receiveAsFlow()

    fun onTriggerEvents(events: RecapProfitEvents) {
        when(events) {
            is RecapProfitEvents.AllRecapProfit -> getAllRecapTrans(dateStart = events.dateStart, dateEnd = events.dateEnd)
            is RecapProfitEvents.RemoveAllRecapProfitMessage -> removeAllRecapTransMessage()
            is RecapProfitEvents.SearchRecap -> findRecap(events.keyword)
        }
    }

    private fun getAllRecapTrans(dateStart: String, dateEnd: String) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .allRecapTrans(dateStart = dateStart, dateEnd = dateEnd)
                        .onEach {
                            it.data?.let { data -> _allRecapTrans.send(data) }
                            it.message?.let { error -> appendRecapTransError(error) }
                            _stateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendRecapTransError(Constants.networkError)
            }
        })
    }

    @OptIn(FlowPreview::class)
    private fun findRecap(keyword: MutableStateFlow<String>) {
        viewModelScope.launch {
            keyword
                .debounce(800)
                .distinctUntilChanged()
                .collectLatest { result ->
                    _keywordInput.send(result)
                }
        }
    }

    private fun appendRecapTransError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }

    private fun removeAllRecapTransMessage() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }
}