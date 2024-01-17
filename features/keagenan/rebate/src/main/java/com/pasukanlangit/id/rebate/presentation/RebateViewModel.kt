package com.pasukanlangit.id.rebate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.rebate.domain.model.RebatePerProductResponse
import com.pasukanlangit.id.rebate.domain.usecase.RebateUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class RebateViewModel(
    private val useCases: RebateUseCases
): ViewModel(){
    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _rebateTotal = MutableStateFlow<String?>(null)
    val rebateTotal: StateFlow<String?> = _rebateTotal

    private val _rebatePerProduct = MutableStateFlow<List<RebatePerProductResponse>?>(null)
    val rebatePerProduct: StateFlow<List<RebatePerProductResponse>?> = _rebatePerProduct

    private val _remainingRebate = MutableStateFlow<String?>(null)
    val remainingRebate: StateFlow<String?> = _remainingRebate

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    fun getRebate(dateStart: String, dateEnd: String){
        _rebateTotal.value = null
        useCases
            .getRemainingRebate()
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { remaining ->
                    _remainingRebate.value = remaining.sum_rebate
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }.launchIn(viewModelScope)

        useCases.
            getRebate(dateStart = dateStart, dateEnd = dateEnd)
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { rebate ->
                    _rebateTotal.value = rebate.totalRebateRupiah
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    fun getRebatePerProduct(dateStart: String, dateEnd: String) {
        _rebatePerProduct.value = null
        useCases
            .getRebatePerProduct(dateStart, dateEnd)
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { rebatePerProduct ->
                    _rebatePerProduct.value = rebatePerProduct
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