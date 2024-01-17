package com.pasukanlangit.cashplus.ui.product

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.UpdateSellingPriceRequest
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class StatusProductViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val context: Context
): ViewModel() {
    private val _productLoading = MutableStateFlow(false)
    val productLoading: StateFlow<Boolean> = _productLoading
    private val _statusProduct = MutableStateFlow<List<ProductModel>?>(null)
    val statusProduct: StateFlow<List<ProductModel>?> = _statusProduct
    private val _productError = MutableStateFlow(ArrayDeque<String>())
    val productError: StateFlow<Queue<String>> = _productError

    private val _updatePriceLoading = MutableStateFlow(false)
    val updatePriceLoading: StateFlow<Boolean> = _updatePriceLoading
    private val _updatePrice = Channel<Boolean?>()
    val updatePrice = _updatePrice.receiveAsFlow()
    private val _updatePriceError = MutableStateFlow(ArrayDeque<String>())
    val updatePriceError: StateFlow<Queue<String>> = _updatePriceError

    fun getProductStatus(uuid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .productStatusUseCase(uuid, accessToken)
                        .onEach {
                            it.data?.let { data -> _statusProduct.value = data }
                            it.message?.let { error -> appendProductError(error) }
                            _productLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendProductError(context.getString(R.string.exception_network))
            }
        })
    }

    fun updateSellingPrice(request: UpdateSellingPriceRequest, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .updateSellingPrice(request, accessToken)
                        .onEach {
                            it.data?.let { data -> _updatePrice.send(data) }
                            it.message?.let { error -> appendUpdatePriceError(error) }
                            _updatePriceLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendUpdatePriceError(context.getString(R.string.exception_network))
            }
        })
    }

    private fun appendProductError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _productError.value = newMessage
    }

    fun removeStatusProductMessage() {
        val currentMessage = productError.value
        currentMessage.remove()
        _productError.value = ArrayDeque(emptyList())
    }

    private fun appendUpdatePriceError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _updatePriceError.value = newMessage
    }

    fun removeUpdatePriceMessage() {
        val currentMessage = updatePriceError.value
        currentMessage.remove()
        _updatePriceError.value = ArrayDeque(emptyList())
    }
}