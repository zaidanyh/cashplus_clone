package com.pasukanlangit.cashplus.ui.injectvoucher

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.DataInject
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.core.model.BalanceResponseCore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class InjectVoucherViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val context: Context
): ViewModel() {
    private val _productLoading = MutableStateFlow(false)
    val productLoading: StateFlow<Boolean> = _productLoading
    private val _productInject = MutableStateFlow<List<ProductModel>?>(null)
    val productInject: StateFlow<List<ProductModel>?> = _productInject
    private val _productError = MutableStateFlow(ArrayDeque<String>())
    val productError: StateFlow<Queue<String>> = _productError

    private val _loadingBalance = MutableStateFlow(false)
    val loadingBalance: StateFlow<Boolean> = _loadingBalance
    private val _balance = MutableStateFlow<BalanceResponseCore?>(null)
    val balance: StateFlow<BalanceResponseCore?> = _balance
    private val _errorBalance = MutableStateFlow(ArrayDeque<String>())
    val errorBalance: StateFlow<Queue<String>> = _errorBalance

    private val _bulkLoading = MutableStateFlow(false)
    val bulkLoading: StateFlow<Boolean> = _bulkLoading
    private val _trxBulk = Channel<Boolean>()
    val trxBulk = _trxBulk.receiveAsFlow()
    private val _bulkError = MutableStateFlow(ArrayDeque<String>())
    val bulkError: StateFlow<Queue<String>> = _bulkError

    fun getProductInject(uuid: String?, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .injectVoucher(uuid = uuid, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _productInject.value = data }
                            it.message?.let { error -> appendProductInjectMessage(error) }
                            _productLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendProductInjectMessage(context.getString(R.string.exception_network))
            }
        })
    }

    fun transactionBulk(
        uuid: String?, codeProduct: String, pin: String, accessToken: String?,
        dataBulk: List<DataInject>
    ) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .transactionBulk(
                            uuid = uuid, codeProduct = codeProduct, pin = pin,
                            dataBulk = dataBulk, accessToken = accessToken
                        )
                        .onEach {
                            it.data?.let { state -> _trxBulk.send(state) }
                            it.message?.let { error -> appendTrxBulkMessage(error) }
                            _bulkLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendTrxBulkMessage(context.getString(R.string.exception_network))
            }
        })
    }

    fun getBalance(uuid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getBalance(uuid = uuid, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _balance.value = data }
                            it.message?.let { message -> appendErrorBalanceMessage(message) }
                            _loadingBalance.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorBalanceMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun appendProductInjectMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _productError.value = newMessage
    }

    fun removeProductInjectMessage() {
        val currentMessage = productError.value
        currentMessage.remove()
        _productError.value = ArrayDeque(emptyList())
    }

    private fun appendTrxBulkMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _bulkError.value = newMessage
    }

    fun removeTrxBulkMessage() {
        val currentMessage = bulkError.value
        currentMessage.remove()
        _bulkError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorBalanceMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _errorBalance.value = newMessage
    }
    fun removeBalanceMessage() {
        val currentMessage = errorBalance.value
        currentMessage.remove()
        _errorBalance.value = ArrayDeque(emptyList())
    }
}