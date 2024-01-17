package com.pasukanlangit.cashplus.ui.pages.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.BalanceAccountResponse
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.UpdateLatLongRequest
import com.pasukanlangit.cashplus.model.response.ProductStoreResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val repository: MainRepository,
    private val context: Context
): ViewModel() {

    private val _banner = MutableLiveData<MyResponse<List<BannerDummy>>>()
    val banner : LiveData<MyResponse<List<BannerDummy>>> = _banner

    private val _bannerCashGold = MutableLiveData<MyResponse<List<BannerDummy>>>()
    val bannerCashGold : LiveData<MyResponse<List<BannerDummy>>> = _bannerCashGold

    private val _bannerGotomekka = MutableLiveData<MyResponse<List<BannerDummy>>>()
    val bannerGotomekka : LiveData<MyResponse<List<BannerDummy>>> = _bannerGotomekka

    private val _products = MutableLiveData<MyResponse<ProductStoreResponse>>()
    val products : LiveData<MyResponse<ProductStoreResponse>> = _products

    private val _balanceAccount = Channel<BalanceAccountResponse?>()
    val balanceAccount = _balanceAccount.receiveAsFlow()

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    fun getBalanceAccount(uuid: String?, accessToken: String?) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .getBalanceDashboard(uuid, accessToken)
                        .onEach {
                            it.message?.let { error ->
                                appendErrorMessage(error)
                            }
                            it.data?.let { data ->
                                _balanceAccount.send(data)
                            }

                            _stateLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                } else
                    appendErrorMessage(context.getString(R.string.exception_network))
            }
        })
    }

    fun updateLatLong(latLngRequest: UpdateLatLongRequest, token: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    viewModelScope.launch(Dispatchers.IO) {
                        repository.updateLatLong(latLngRequest, token)
                    }
                }
            }
        })
    }

    fun getBanner(){
        viewModelScope.launch(Dispatchers.IO)  {
            _banner.postValue(MyResponse.loading(null))
            try {
                _banner.postValue(MyResponse.success(getBannerDummy()))
            } catch (ex: Exception) {
                _banner.postValue(MyResponse.success(getBannerDummy()))
            }
        }
    }

    fun getBannerCashGold() {
        viewModelScope.launch(Dispatchers.IO)  {
            _bannerCashGold.postValue(MyResponse.loading(null))

            delay(1200)
            _bannerCashGold.postValue(MyResponse.success(getBannerCashGoldDummy()))
        }
    }

    fun getBannerGotomekka() {
        viewModelScope.launch(Dispatchers.IO)  {
            _bannerGotomekka.postValue(MyResponse.loading(null))

            delay(1200)
            _bannerGotomekka.postValue(MyResponse.success(getBannerGotomekkaDummy()))
        }
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = newMessage
    }

    fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }
}