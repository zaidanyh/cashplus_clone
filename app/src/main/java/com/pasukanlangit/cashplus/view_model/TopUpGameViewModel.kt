package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class TopUpGameViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _productList = MutableLiveData<MyResponse<ProductResultModel>>()
    val productList: LiveData<MyResponse<ProductResultModel>> = _productList

    private val _menuGames = MutableLiveData<MyResponse<ProductResultModel>>()
    val menuGames: LiveData<MyResponse<ProductResultModel>> = _menuGames

    fun getMenu(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _menuGames.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _menuGames.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _menuGames.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            } catch (ex : Exception) {
                _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getProduct(productRequest: ProductRequest, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO)  {
            _productList.postValue(MyResponse.loading(null))
            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productList.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productList.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productList.postValue(MyResponse.error(context.getString(R.string.exception_gson) , null))
                    }

                }
            }catch (ex : Exception){
                _productList.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}