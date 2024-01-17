package com.pasukanlangit.id.nobu.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.nobu.domain.model.SendResultScanResponse
import com.pasukanlangit.id.nobu.domain.usecase.NobuUseCases
import kotlinx.coroutines.flow.*
import java.util.*

class ScanViewModel(
    private val useCases: NobuUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _sendResultScan = MutableStateFlow<SendResultScanResponse?>(null)
    val sendResultScan: StateFlow<SendResultScanResponse?> = _sendResultScan

    fun sendResultQris(resultScan: String) {
        _sendResultScan.value = null
        useCases
            .sendResultScan(resultScan)
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { data ->
                    _sendResultScan.value = data
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }
            }.launchIn(viewModelScope)
    }

    fun removeHeadQueue() {
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