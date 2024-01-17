package com.pasukanlangit.cashplus.ui.all_menus

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.OprNameRequest
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.model.response.ServerIdGamesResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class DetailMenusViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _product = MutableLiveData<MyResponse<ProductResultModel>>()
    val product: LiveData<MyResponse<ProductResultModel>> = _product

    private val _serverName = MutableLiveData<MyResponse<ServerIdGamesResponse>>()
    val serverName: LiveData<MyResponse<ServerIdGamesResponse>> = _serverName

    fun getProduct(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _product.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _product.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val  message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _product.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _product.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _product.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getServerGamesId(oprNameRequest: OprNameRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _serverName.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getServerIdGames(oprNameRequest, accessToken)

                if(response.code() == 200){
                    _serverName.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val  message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _serverName.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _serverName.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _serverName.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}