package com.pasukanlangit.id.ui_downline_home.priceplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.ui_downline_home.utils.MarkupPlanParcelable
import com.pasukanlangit.id.ui_downline_home.utils.SetMarkupDataClass
import com.pasukanlangit.id.ui_downline_home.utils.toListMarkupPlanParcelable
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import java.util.Queue

class PricePlanViewModel(
    private val useCases: DownLineUseCases
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _pricePlans = MutableStateFlow<List<MarkupPlanParcelable>?>(null)
    val pricePlans: StateFlow<List<MarkupPlanParcelable>?> = _pricePlans
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    val keywordInput = MutableStateFlow("")

    private val _loadingCreate = MutableStateFlow(false)
    val loadingCreate: StateFlow<Boolean> = _loadingCreate
    private val _createReplaceMarkup = Channel<Boolean?>()
    val createReplaceMarkup = _createReplaceMarkup.receiveAsFlow()
    private val _errorCreate = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorCreate: StateFlow<Queue<String>> = _errorCreate

    private val _loadingApply = MutableStateFlow(false)
    val loadingApply: StateFlow<Boolean> = _loadingApply
    private val _applyMarkupPlan = Channel<Boolean?>()
    val applyMarkupPlan = _applyMarkupPlan.receiveAsFlow()
    private val _errorApply = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorApply: StateFlow<Queue<String>> = _errorApply

    private val _loadingUnApply = MutableStateFlow(false)
    val loadingUnApply: StateFlow<Boolean> = _loadingUnApply
    private val _unApplyMarkupPlan = Channel<Boolean?>()
    val unApplyMarkupPlan = _unApplyMarkupPlan.receiveAsFlow()
    private val _errorUnApply = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorUnApply: StateFlow<Queue<String>> = _errorUnApply

    private val _loadingDelete = MutableStateFlow(false)
    val loadingDelete: StateFlow<Boolean> = _loadingDelete
    private val _deleteMarkupPlan = Channel<Boolean?>()
    val deleteMarkupPlan = _deleteMarkupPlan.receiveAsFlow()
    private val _errorDelete = MutableStateFlow(ArrayDeque<String>(listOf()))
    val errorDelete: StateFlow<Queue<String>> = _errorDelete

    val markupDataClass = MutableStateFlow<SetMarkupDataClass?>(null)

    fun onTriggerEvent(event: PricePlanEvent) {
        when(event) {
            is PricePlanEvent.GetListPricePlan -> getPricePlans()
            is PricePlanEvent.RemoveListPricePlanMessage -> removePricePlansError()
            is PricePlanEvent.CreateReplaceMarkupPlan ->
                createReplaceMarkup(event.markupCode, event.description, event.isAddProduct)
            is PricePlanEvent.RemoveCreateMessage -> removeCreateError()
            is PricePlanEvent.ApplyMarkupPlan ->
                applyMarkupPlan(event.markupCode, event.downLinePhone)
            is PricePlanEvent.RemoveApplyMessage -> removeApplyError()
            is PricePlanEvent.UnApplyMarkupPlan -> unApplyMarkupPlan(event.downLinePhone)
            is PricePlanEvent.RemoveUnApplyMessage -> removeUnApplyError()
            is PricePlanEvent.DeleteMarkupPlan -> deleteMarkupPlan(event.markupCode)
            is PricePlanEvent.RemoveDeleteMessage -> removeDeleteError()
        }
    }

    fun setMarkupDataClass(markupCode: String, description: String) {
        markupDataClass.value = SetMarkupDataClass(markupCode, description)
    }

    @OptIn(FlowPreview::class)
    private fun getPricePlans() {
        if (keywordInput.value.isEmpty()) {
            useCases
                .getMarkupPlans()
                .onEach {
                    it.data?.let { data -> _pricePlans.value = data.toListMarkupPlanParcelable() }
                    it.message?.let { error -> appendPricePlansError(error) }
                    _isLoading.value = it.isLoading
                }.launchIn(viewModelScope)
            return
        }
        viewModelScope.launch {
            keywordInput
                .debounce(800)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collectLatest { filterKey ->
                    useCases
                        .getMarkupPlans(filterKey)
                        .onEach {
                            it.data?.let { data -> _pricePlans.value = data.toListMarkupPlanParcelable() }
                            it.message?.let { error -> appendPricePlansError(error) }
                            _isLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                }
        }
    }

    private fun createReplaceMarkup(markupCode: String, desc: String, isAddProduct: Boolean) {
        useCases
            .createReplaceMarkupPlan(markupCode = markupCode, dsc = desc, isAddProduct = isAddProduct)
            .onEach {
                it.data?.let { state -> _createReplaceMarkup.send(state) }
                it.message?.let { error -> appendCreateError(error) }
                _loadingCreate.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun applyMarkupPlan(markupCode: String, downLinePhone: String) {
        useCases
            .applyMarkupPlan(markupCode = markupCode, downlinePhone = downLinePhone)
            .onEach {
                it.data?.let { state -> _applyMarkupPlan.send(state) }
                it.message?.let { error -> appendApplyError(error) }
                _loadingApply.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun unApplyMarkupPlan(downLinePhone: String) {
        useCases
            .unApplyMarkupPlan(downlinePhone = downLinePhone)
            .onEach {
                it.data?.let { state -> _unApplyMarkupPlan.send(state) }
                it.message?.let { error -> appendUnApplyError(error) }
                _loadingUnApply.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun deleteMarkupPlan(markupCode: String) {
        useCases
            .deleteMarkupPlan(markupCode = markupCode)
            .onEach {
                it.data?.let { state -> _deleteMarkupPlan.send(state) }
                it.message?.let { error -> appendDeleteError(error) }
                _loadingDelete.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun appendPricePlansError(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }
    private fun removePricePlansError() {
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

    private fun appendApplyError(error: String) {
        val currentMessage = errorApply.value
        currentMessage.add(error)
        _errorApply.value = ArrayDeque(currentMessage)
    }
    private fun removeApplyError() {
        val currentMessage = errorApply.value
        currentMessage.remove()
        _errorApply.value = ArrayDeque(currentMessage)
    }

    private fun appendUnApplyError(error: String) {
        val currentMessage = errorUnApply.value
        currentMessage.add(error)
        _errorUnApply.value = ArrayDeque(currentMessage)
    }

    private fun removeUnApplyError() {
        val currentMessage = errorUnApply.value
        currentMessage.remove()
        _errorUnApply.value = ArrayDeque(currentMessage)
    }

    private fun appendDeleteError(error: String) {
        val current = errorDelete.value
        current.add(error)
        _errorDelete.value = ArrayDeque(current)
    }
    private fun removeDeleteError() {
        val current = errorDelete.value
        current.remove()
        _errorDelete.value = ArrayDeque(current)
    }
}