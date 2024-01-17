package com.pasukanlangit.id.nobu.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.nobu.domain.model.HistoryTrxResponse
import com.pasukanlangit.id.nobu.domain.usecase.NobuUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class HistoryNobuViewModel(
    private val useCases: NobuUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _nobuHistory = MutableStateFlow<HistoryTrxResponse?>(null)
    val nobuHistory: StateFlow<HistoryTrxResponse?> = _nobuHistory

    private val _filterTitle = MutableStateFlow<String?>("")
    val filterTitle: StateFlow<String?> = _filterTitle
    private val _dateStart = MutableStateFlow<String?>("")
    private val _dateEnd = MutableStateFlow<String?>("")

    fun onTriggerEvent(event: HistoryNobuEvent) {
        when(event) {
            is HistoryNobuEvent.FilterByDateHistory -> {
                _dateStart.value = event.dateStart
                _dateEnd.value = event.dateEnd

                getHistoryTrx()
            }
            is HistoryNobuEvent.RemoveHeadMessage -> removeHeadQueue()
        }
    }

    fun setFilterTitle(value: String? = null) {
        _filterTitle.value = value
    }

    private fun getHistoryTrx() {
        _nobuHistory.value = null
        useCases
            .historyNobuUseCase(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value
            )
            .onEach {
                _stateLoading.value = it.isLoading

                it.message?.let { error ->
                    appendErrorMessage(error)
                }
                it.data?.let { data ->
                    _nobuHistory.value = data
                }
            }.launchIn(viewModelScope)
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }
}