package com.pasukanlangit.id.ui_downline_home.priceplan.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.domain_downline.model.ProductMarkup
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.ui_downline_home.utils.toListProductMarkup
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.ArrayDeque

class ProductPricePlanViewModel(
    private val useCases: DownLineUseCases
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _productPricePlan = MutableStateFlow<List<MarkupProductResponse>?>(null)
    val  productPricePlan: StateFlow<List<MarkupProductResponse>?> = _productPricePlan
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    // Change markup product
    private val _loadingCreate = MutableStateFlow(false)
    val loadingCreate: StateFlow<Boolean> = _loadingCreate
    private val _createReplace = Channel<Boolean>()
    val createReplace = _createReplace.receiveAsFlow()
    private val _errorCreate = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorCreate: StateFlow<Queue<String>> = _errorCreate

    fun onTriggerEvent(event: ProductPricePlanEvent) {
        when(event) {
            is ProductPricePlanEvent.GetProductPricePlan -> productPricePlan(event.codeMarkup)
            is ProductPricePlanEvent.RemoveHeadMessage -> removeErrorMessage()
            // Change markup from product list
            is ProductPricePlanEvent.ChangeMarkupProduct ->
                changeMarkup(event.markupCode, event.markupDesc, event.productCode, event.markupValue)
            is ProductPricePlanEvent.DeleteMarkupProduct ->
                deleteMarkupProduct(event.markupCode, event.markupDesc, event.productCode, event.dataProduct)
            is ProductPricePlanEvent.RemoveMessageChangeMarkup -> removeCreateError()
        }
    }

    private fun productPricePlan(codeMarkup: String) {
        _productPricePlan.value = null
        useCases
            .getDetailMarkupPlan(codeMarkupPlan = codeMarkup)
            .onEach {
                it.data?.let { data -> _productPricePlan.value = data }
                it.message?.let { error -> appendErrorMessage(error) }
                _isLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun changeMarkup(
        markupCode: String, markupDesc: String,
        productCode: String, markupValue: String
    ) {
        val products = productPricePlan.value
        products?.find { it.kodeProduct == productCode }?.markup = markupValue
        useCases
            .createReplaceMarkupPlan(markupCode = markupCode, dsc = markupDesc, products = products?.toListProductMarkup())
            .onEach {
                it.data?.let { data -> _createReplace.send(data) }
                it.message?.let { error -> appendCreateError(error) }
                _loadingCreate.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun deleteMarkupProduct(markupCode: String, markupDesc: String, productCode: String, dataProduct: List<ProductMarkup>) {
        val products = dataProduct.toMutableList()
        val find = products.find { it.productCode == productCode }
        products.remove(find)
        useCases
            .createReplaceMarkupPlan(
                markupCode = markupCode, dsc = markupDesc, products = products.toList()
            ).onEach {
                it.data?.let { data -> _createReplace.send(data) }
                it.message?.let { error -> appendCreateError(error) }
                _loadingCreate.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }

    private fun removeErrorMessage() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }

    private fun appendCreateError(error: String) {
        val currentMessage = errorCreate.value
        currentMessage.add(error)
        _errorCreate.value = ArrayDeque(currentMessage)
    }
    private fun removeCreateError() {
        val current = errorCreate.value
        current.remove()
        _errorCreate.value = ArrayDeque(current)
    }
}