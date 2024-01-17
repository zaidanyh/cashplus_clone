package com.pasukanlangit.id.ui_cashgold_navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DashboardGoldViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){
    private val _stateStatusRegister = MutableStateFlow<Boolean?>(null)
    val stateStatusRegister: StateFlow<Boolean?> = _stateStatusRegister

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    fun checkRegisterStatus() {
        //because flow cannot emit same value, so need to reset value before emitting actual value
        viewModelScope.launch {
            _stateStatusRegister.value = null
            useCases
                .checkStatusRegister()
                .collectLatest {
                    _stateLoading.value = it.isLoading
                    it.data?.let { status ->
                        _stateStatusRegister.value = status
                    }
                    it.message?.let { errorMessage ->
                        appendErrorMessage(errorMessage)
                    }
                }

        }
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }


    fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }
}