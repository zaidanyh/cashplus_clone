package com.pasukanlangit.cashplus.ui.pages.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.PrintReceiptResponse
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.OrderCheckTokoRequest
import com.pasukanlangit.cashplus.model.response.PrintTrxResponse
import com.pasukanlangit.cashplus.model.response.SellingPriceResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HistoryDetailViewModel(
    private val networkConnectivity: NetworkConnectivity,
    private val mainRepository: MainRepository,
    private val useCases: AppUseCases,
    private val context: Context
) : ViewModel() {
    private val _receipt = MutableLiveData<MyResponse<PrintTrxResponse>>()
    val receipt : LiveData<MyResponse<PrintTrxResponse>> = _receipt

    private val _printLoading = MutableStateFlow(false)
    val printLoading: StateFlow<Boolean> = _printLoading
    private val _printReceipt = Channel<PrintReceiptResponse?>()
    val printReceipt = _printReceipt.receiveAsFlow()
    private val _sellingLoading = MutableStateFlow(false)
    val sellingLoading: StateFlow<Boolean> = _sellingLoading
    private val _sellingPrice = Channel<SellingPriceResponse?>()
    val sellingPrice = _sellingPrice.receiveAsFlow()

    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    fun printReciept(checkTokoRequest: OrderCheckTokoRequest, token: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _receipt.postValue(MyResponse.loading(null))

                        try {
                            val response = mainRepository.printReciept(checkTokoRequest, token)

                            if (response.code() == 200) {
                                _receipt.postValue(MyResponse.success(response.body()))
                            } else {
                                _receipt.postValue(
                                    MyResponse.error(
                                        context.getString(R.string.exception_gson),
                                        null
                                    )
                                )
                            }
                        } catch (ex: Exception) {
                            _receipt.postValue(
                                MyResponse.error(
                                    ex.message ?: Constants.unknownError,
                                    null
                                )
                            )
                        }
                    }
                } else {
                    _receipt.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
                }
            }
        })
    }

    fun getSellingPrice(uuid: String?, codeProduct: String, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getSellingPrice(uuid = uuid, productCode = codeProduct, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _sellingPrice.send(data) }
                            it.message?.let { error -> appendHeadMessage(error) }
                            _sellingLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendHeadMessage(Constants.networkError)
            }
        })
    }

    fun newPrintReceipt(uuid: String?, productCode: String, trxId: String, adjustPrice: String, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .printReceiptUseCase(
                            uuid = uuid, productCode = productCode, trxId = trxId,
                            adjustPrice = adjustPrice, accessToken = accessToken
                        ).onEach {
                            it.data?.let { data -> _printReceipt.send(data) }
                            it.message?.let { error -> appendHeadMessage(error) }
                            _printLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendHeadMessage(Constants.networkError)
            }
        })
    }

    private fun appendHeadMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }

    fun removePrintError() {
        val currentError = stateError.value
        currentError.remove()
        _stateError.value = ArrayDeque(emptyList())
    }
}