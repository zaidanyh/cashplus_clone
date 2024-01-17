package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.domain_downline.model.ProductMarkup
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.ui_downline_home.utils.SummaryProduct
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.ArrayDeque

class FindProductViewModel(
    private val useCases: DownLineUseCases
): ViewModel() {
    private val _loadingSearch = MutableStateFlow(false)
    val loadingSearch: StateFlow<Boolean> = _loadingSearch
    private val _productSearch = MutableStateFlow<List<MarkupProductResponse>?>(null)
    val productSearch: StateFlow<List<MarkupProductResponse>?> = _productSearch
    private val _errorProductSearch = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorProductSearch : StateFlow<Queue<String>> = _errorProductSearch

    // adding product data
    private val _addMarkupProduct = MutableStateFlow<MutableList<SummaryProduct>>(mutableListOf())
    val addMarkupProduct: StateFlow<List<SummaryProduct>> = _addMarkupProduct

    private val _loadingCreate = MutableStateFlow(false)
    val loadingCreate: StateFlow<Boolean> = _loadingCreate
    private val _createReplaceMarkup = Channel<Boolean?>()
    val createReplaceMarkup = _createReplaceMarkup.receiveAsFlow()
    private val _errorCreate = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorCreate: StateFlow<Queue<String>> = _errorCreate

    fun onTriggerEvent(event: FindProductEvent) {
        when(event) {
            is FindProductEvent.GetProductPricePlanBySearch ->
                productPricePlanSearch(event.codeMarkup, event.keyword, event.mainMarkup)
            is FindProductEvent.RemoveHeadMessageSearch -> removeErrorSearch()
            is FindProductEvent.AddProductMarkup ->
                addMarkupProduct(event.productCode, event.markupValue, event.category, event.positionIndex)
            is FindProductEvent.RemoveMarkupProduct -> removeMarkupProduct(event.productCode)
            is FindProductEvent.ChangeProductMarkup ->
                changeMarkupProduct(event.productCode, event.markupValue)
            is FindProductEvent.ClearProductMarkup -> clearMarkupForPricePlan()
            is FindProductEvent.CreateReplace -> createReplaceMarkup(event.markupCode, event.description, event.products)
            is FindProductEvent.RemoveCreateMessage -> removeCreateError()
        }
    }

    private fun productPricePlanSearch(codeMarkup: String, keyword: String, mainMarkup: String) {
        useCases
            .findProductMarkup(codeMarkup = codeMarkup, keyword = keyword, mainMarkup = mainMarkup)
            .onEach {
                it.data?.let { data -> _productSearch.value = data }
                it.message?.let { error -> appendErrorSearch(error) }
                _loadingSearch.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun addMarkupProduct(productCode: String, markupValue: String, category: String, positionIndex: Int?) {
        val currentValue = addMarkupProduct.value
        val findSameValue = currentValue.find { it.codeProduct == productCode }
        if (findSameValue != null) return
        val markupProduct = SummaryProduct(
            codeProduct = productCode, markup = markupValue,
            category = category, positionIndex = positionIndex
        )

        val newItem = (currentValue + markupProduct).toMutableList()
        _addMarkupProduct.value = newItem
    }

    private fun removeMarkupProduct(productCode: String) {
        val products = addMarkupProduct.value
        val find = products.find { it.codeProduct == productCode } ?: return
        val newItem = (products - find).toMutableList()
        _addMarkupProduct.value = newItem
    }

    private fun changeMarkupProduct(productCode: String, markupValue: String) {
        val current = addMarkupProduct.value
        current.find { it.codeProduct == productCode }?.markup = markupValue
        _addMarkupProduct.value = current.toMutableList()
    }

    private fun clearMarkupForPricePlan() {
        if (addMarkupProduct.value.isNotEmpty()) _addMarkupProduct.value.clear()
    }

    private fun createReplaceMarkup(markupCode: String, desc: String, products: List<ProductMarkup>?) {
        useCases
            .createReplaceMarkupPlan(markupCode = markupCode, dsc = desc, products = products, isAddProduct = true)
            .onEach {
                it.data?.let { data -> _createReplaceMarkup.send(data) }
                it.message?.let { error -> appendCreateError(error) }
                _loadingCreate.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun appendErrorSearch(error: String) {
        val currentMessage = errorProductSearch.value
        currentMessage.add(error)
        _errorProductSearch.value = ArrayDeque(currentMessage)
    }

    private fun removeErrorSearch() {
        val currentMessage = errorProductSearch.value
        currentMessage.remove()
        _errorProductSearch.value = ArrayDeque(currentMessage)
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