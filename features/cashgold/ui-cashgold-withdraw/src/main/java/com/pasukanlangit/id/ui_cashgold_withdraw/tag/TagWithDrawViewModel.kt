package com.pasukanlangit.id.ui_cashgold_withdraw.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.Address
import com.pasukanlangit.id.model.TrxGoldResponse
import com.pasukanlangit.id.usecase.CashGoldUseCase
import com.pasukanlangit.id.usecase.MainAddress
import com.pasukanlangit.id.usecase.TrxProvider
import com.pasukanlangit.id.usecase.TrxType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class TagWithDrawViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateMainAddress = MutableStateFlow<Address?>(null)
    val stateMainAddress: StateFlow<Address?> = _stateMainAddress

    private val _isEmptyMainAddress = MutableStateFlow<Boolean?>(null)
    val isEmptyMainAddress: StateFlow<Boolean?> = _isEmptyMainAddress

    private val _isKtpNotEmpty = MutableStateFlow<Boolean?>(null)
    val isKtpNotEmpty: StateFlow<Boolean?> = _isKtpNotEmpty

    private val _trxTagResult = MutableStateFlow<TrxGoldResponse?>(null)
    val trxTagResult: StateFlow<TrxGoldResponse?> = _trxTagResult

    private val _trxPayResult = MutableStateFlow<TrxGoldResponse?>(null)
    val trxPayResult: StateFlow<TrxGoldResponse?> = _trxPayResult

    private val _productWithDraw = MutableStateFlow<ProductWithDraw?>(null)

    private val _bookWithDrawId = MutableStateFlow<String?>(null)
    val bookWithDrawId: StateFlow<String?> = _bookWithDrawId

    fun onTriggerEvent(event: TagWithDrawEvent){
        when(event){
            is TagWithDrawEvent.GetMainAddress -> {
                getMainAddress()
            }
            is TagWithDrawEvent.RemoveHeadMessageQueue -> {
                removeHeadQueue()
            }
            is TagWithDrawEvent.CheckIsKtpIsEmpty -> {
                checkInputKtpIsEmpty()
            }
            is TagWithDrawEvent.SendTrxGold -> {
                sendTrx(event.type, event.pin)
            }
            is TagWithDrawEvent.WithDrawBook -> {
                withDrawBook()
            }
            is TagWithDrawEvent.SetWithDrawProduct -> {
                _productWithDraw.value = event.withDrawProduct
            }
        }
    }

    private fun withDrawBook() {
        _bookWithDrawId.value = null
        val address = stateMainAddress.value
        val productWithDraw = _productWithDraw.value

        useCases
            .withDrawBook(
                addressId = address?.id.toString(),
                denomination = productWithDraw?.productDominationRaw,
                providerId = productWithDraw?.providerId,
                quantity = productWithDraw?.qty
            )
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { withDrawBook ->
                    _bookWithDrawId.value = withDrawBook.withdrawId
                }
                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun sendTrx(type: TrxType, pin: String) {
        resetStateTrx(type)
        useCases
            .sendTrx(
                trxProvider = TrxProvider.WithDrawGold(type, bookWithDrawId.value),
                pin = pin
            )
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { data ->
                    setStateTrx(data, type)
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkInputKtpIsEmpty() {
        _isKtpNotEmpty.value = null
        useCases
            .checkKtpNotEmpty()
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { value ->
                    _isKtpNotEmpty.value = value
                }
                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getMainAddress() {
        useCases
            .mainAddress()
            .onEach {
                _stateLoading.value = it.isLoading

                it.data?.let { data ->
                    if(data != MainAddress.addressEmpty){
                        _stateMainAddress.value = data
                    }
                    _isEmptyMainAddress.value = !it.isLoading && it.data == MainAddress.addressEmpty
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

}