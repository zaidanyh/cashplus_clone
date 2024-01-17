package com.pasukanlangit.id.ui_cashgold_buy.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.ChartResponse
import com.pasukanlangit.id.usecase.CashGoldUseCase
import com.pasukanlangit.id.usecase.ChartDuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CashGoldHomeViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){

    private var getChartJob: Job? = null

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateGoldPrice = MutableStateFlow<String?>(null)
    val stateGoldPrice: StateFlow<String?> = _stateGoldPrice

    private val _showKycNotif = MutableStateFlow<Boolean?>(null)
    val showKycNotif: StateFlow<Boolean?> = _showKycNotif

    private val _stateBalance = MutableStateFlow<String?>(null)
    val stateBalance: StateFlow<String?> = _stateBalance

    private val _stateChart = MutableStateFlow(listOf<ChartResponse>())
    val stateChart: StateFlow<List<ChartResponse>> = _stateChart

    private val _stateStatusRegister = MutableSharedFlow<Boolean?>()
    val stateStatusRegister: SharedFlow<Boolean?> = _stateStatusRegister


//    init {
//        onTriggerEvent(HomeEvent.GetUserBalance)
//        onTriggerEvent(HomeEvent.GetGoldPrice)
//        onTriggerEvent(HomeEvent.GetChart(ChartDuration.A_MONTH))
//    }

    fun onTriggerEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.GetUserBalance -> {
                getUserBalance()
            }
            is HomeEvent.GetGoldPrice -> {
                getGoldPrice()
            }
            is HomeEvent.GetChart -> {
                getChart(event.chartDuration)
            }
            is HomeEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is HomeEvent.CheckStatusRegister -> {
                checkRegisterStatus()
            }
            is HomeEvent.CheckKycStatus -> {
                checkKycStatus()
            }
        }
    }

    private fun checkKycStatus() {
        _showKycNotif.value = null
        useCases
            .kycStatus()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { kycStatus ->
                    val notUpload =  !kycStatus.isIdentityCardUploaded &&
                            !kycStatus.isIdentityWithSelfieUploaded &&
                            !kycStatus.isTaxIdUploaded

                    _showKycNotif.value = kycStatus.isRejected || notUpload
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkRegisterStatus() {
        //because flow cannot emit same value, so need to reset value before emitting actual value
        viewModelScope.launch {
            _stateStatusRegister.emit(null)
            useCases
                .checkStatusRegister()
                .collectLatest {
                    _stateLoading.value = it.isLoading
                    it.data?.let { status ->
                        _stateStatusRegister.emit(status)
                    }
                    it.message?.let { errorMessage ->
                        appendErrorMessage(errorMessage)
                    }
                }

        }

    }

    private fun getChart(chartDuration: ChartDuration) {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch {
            useCases
                .getChartUseCase(chartDuration)
                .onEach {
                    _stateLoading.value = it.isLoading
                    it.data?.let { listChart ->
                        _stateChart.value = listChart
                    }
                    it.message?.let { errorMessage ->
                        appendErrorMessage(errorMessage)
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun getGoldPrice() {
        useCases
            .getGoldPrice()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.priceBuyCurrency?.let { priceBuy ->
                    _stateGoldPrice.value = priceBuy
                }
                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getUserBalance(){
        useCases
            .getIndoGoldBalance()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { userBalance ->
                    _stateBalance.value = userBalance.gold
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }


    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }
}