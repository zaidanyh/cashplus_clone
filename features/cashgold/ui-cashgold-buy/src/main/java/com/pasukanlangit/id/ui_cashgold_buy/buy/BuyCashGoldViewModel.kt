package com.pasukanlangit.id.ui_cashgold_buy.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.model.GoldPrice
import com.pasukanlangit.id.model.LockGoldType
import com.pasukanlangit.id.model.TrxGoldResponse
import com.pasukanlangit.id.usecase.CashGoldUseCase
import com.pasukanlangit.id.usecase.TrxProvider
import com.pasukanlangit.id.usecase.TrxType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@FlowPreview
class BuyCashGoldViewModel(
    private val useCases: CashGoldUseCase
): ViewModel() {

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateGoldBalance = MutableStateFlow<String?>(null)
    val stateGoldBalance: StateFlow<String?> = _stateGoldBalance

    private val _stateUserBalance = MutableStateFlow<String?>(null)
    val stateUserBalance: StateFlow<String?> = _stateUserBalance

    private val _stateGoldPrice = MutableStateFlow<GoldPrice?>(null)
    val stateGoldPrice: StateFlow<GoldPrice?> = _stateGoldPrice

    val inputOnChange = MutableStateFlow<String?>(null)

    private val _inputNominal = MutableStateFlow<String?>(null)
    val inputNominal: StateFlow<String?> = _inputNominal

//    private val _inputRightResult = MutableStateFlow<String?>(null)
//    val inputRightResult: StateFlow<String?> = _inputRightResult

    private val _lockResult = MutableStateFlow<String?>(null)
    val lockResult: StateFlow<String?> = _lockResult

    private val _lockType = MutableStateFlow(LockGoldType.BY_PRICE)
    val lockType: StateFlow<LockGoldType> = _lockType

    private val _kycStatus = MutableStateFlow<KycStatus?>(null)
    val kycStatus: StateFlow<KycStatus?> = _kycStatus

    private val _trxTagResult = MutableStateFlow<TrxGoldResponse?>(null)
    val trxTagResult: StateFlow<TrxGoldResponse?> = _trxTagResult

    private val _trxPayResult = MutableStateFlow<TrxGoldResponse?>(null)
    val trxPayResult: StateFlow<TrxGoldResponse?> = _trxPayResult

    init {
        observeInputOnChange()
    }

    fun onTriggerEvent(event: BuyEvent){
        when(event){
            is BuyEvent.GetGoldBalance -> {
                getGoldBalance()
            }
            is BuyEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is BuyEvent.GetGoldPrice -> {
                getGoldPrice()
            }
            is BuyEvent.GetSt24Balance -> {
                getSt24Balance()
            }
//            is BuyEvent.SwapInput -> {
//                swapInput()
//            }
            is BuyEvent.CheckKyc -> {
                checkKycStatus()
            }
            is BuyEvent.LockGold -> {
                lockGold(event.nominal)
            }
            is BuyEvent.SendTrxGold -> {
                sendTrx(event.pin, event.type)
            }
            is BuyEvent.SetLockGoldType -> {
                _lockType.value = event.type
            }
        }
    }

    private fun sendTrx(pin: String, type: TrxType) {
        resetStateTrx(type)
        val trxProvider = when(lockType.value){
            LockGoldType.BY_PRICE -> {
                TrxProvider.BuyGoldRupiah(
                    trxType = type,
                    dest = lockResult.value
                )
            }
            LockGoldType.BY_GRAM -> {
                TrxProvider.BuyGoldGram(
                    trxType = type,
                    dest = lockResult.value
                )
            }
        }
        useCases
            .sendTrx(
                trxProvider = trxProvider,
                pin = pin
            )
//            .sendTrx(
//                type = type,
//                lockType = lockType.value,
//                destination = lockResult.value,
//                pin = pin
//            )
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { data ->
                    setStateTrx(data, type)
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                    getGoldPrice()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setStateTrx(data: TrxGoldResponse, type: TrxType) {
        when(type){
            TrxType.TAG -> _trxTagResult.value = data
            TrxType.PAY -> _trxPayResult.value = data
        }
    }

    private fun resetStateTrx(type: TrxType) {
        when(type){
            TrxType.TAG -> _trxTagResult.value = null
            TrxType.PAY -> _trxPayResult.value = null
        }
    }
//
//    private fun observeInputOnChange() {
//        viewModelScope.launch {
//            inputOnChange
//                .debounce(800)
//                .distinctUntilChanged()
//                .map {
//                    when(lockType.value){
//                        LockGoldType.BY_PRICE -> {
//                            it?.trim()?.filter { char -> char.isDigit() }
//                        }
//                        LockGoldType.BY_GRAM -> {
//                            it?.trim()
//                        }
//                    }
//                }
//                .collectLatest {
//                    _inputLeftResult.value = it
//                    if(!it.isNullOrEmpty()) calculateInputRight(it)
//                }
//        }
//    }

    private fun observeInputOnChange() {
        viewModelScope.launch {
            inputOnChange
                .collectLatest {
                    _inputNominal.value = it
                }
        }
    }

//    private fun calculateInputRight(inputLeftString: String) {
//        val goldPrice = stateGoldPrice.value?.priceBuyInt
//        val inputLeft = inputLeftString
//            .replace("Rp","",ignoreCase = true)
//            .replace(",", ".")
//            .toDoubleOrNull()
//        if(goldPrice == null || goldPrice == 0){
//            appendErrorMessage("Gagal mendapatkan harga emas")
//        }else if(inputLeft == null){
//            _inputLeftResult.value = null
//            _inputRightResult.value = null
//        }
//        else{
//            val valueRight = getValueInputRight(inputLeft, goldPrice)
//            _inputRightResult.value =  valueRight
//        }
//
//    }

//    private fun getValueInputRight(inputLeft: Double, goldPrice: Int): String {
//        return if(lockType.value == LockGoldType.BY_PRICE){
//            val gram = inputLeft / goldPrice.toDouble()
//            val isGramInteger = (gram == floor(gram))
//            if(isGramInteger) {
//                gram.toInt().toString()
//            }else{
//                String.format("%.4f", gram)
//            }
//        }else{
//            getNumberRupiah(inputLeft.times(goldPrice))
//        }
//    }

    private fun lockGold(nominal: String?) {
        useCases
            .lockGold(
                value = nominal,
                goldPrice = stateGoldPrice.value?.priceBuyInt,
                lockGoldType = lockType.value
             )
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { lock ->
                    _lockResult.value = lock.lockId
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                    getGoldPrice()
                }
            }
            .launchIn(viewModelScope)
    }


    private fun checkKycStatus() {
        _kycStatus.value = null
        useCases
            .kycStatus()
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { kycStatus ->
                    _kycStatus.value = kycStatus
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }


//
//    private fun swapInput() {
//        _lockType.value = if(lockType.value == LockGoldType.BY_PRICE) LockGoldType.BY_GRAM else LockGoldType.BY_PRICE
//        //swap value
//        val leftValue = inputLeftResult.value
//        _inputLeftResult.value = inputRightResult.value
//        _inputRightResult.value = leftValue
//    }

    private fun getSt24Balance() {
        useCases
            .getSt24Profile()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { userBalance ->
                    _stateUserBalance.value = userBalance.balanceCurrency
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }


    private fun getGoldBalance(){
        useCases
            .getIndoGoldBalance()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { userBalance ->
                    _stateGoldBalance.value = userBalance.gold
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getGoldPrice() {
        useCases
            .getGoldPrice()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let {  goldPrice ->
                    _stateGoldPrice.value = goldPrice
                }
                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }

            }
            .launchIn(viewModelScope)
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