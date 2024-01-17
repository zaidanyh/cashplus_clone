package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class TopupProviderViewModel(private val mainRepository: MainRepository,private val context:Context) : ViewModel() {
    private val _numberProvider = MutableLiveData<Pair<String, Boolean>>()
    val numberProvider : LiveData<Pair<String, Boolean>> = _numberProvider

    private val _productPulsa = MutableLiveData<MyResponse<ProductResultModel>>()
    val productPulsa: LiveData<MyResponse<ProductResultModel>> = _productPulsa

    private val _productPaketData = MutableLiveData<MyResponse<ProductResultModel>>()
    val productPaketData: LiveData<MyResponse<ProductResultModel>> = _productPaketData

    private val _productPaketTelp = MutableLiveData<MyResponse<ProductResultModel>>()
    val productPaketTelp: LiveData<MyResponse<ProductResultModel>> = _productPaketTelp

    private val _productSms = MutableLiveData<MyResponse<ProductResultModel>>()
    val productSms: LiveData<MyResponse<ProductResultModel>> = _productSms

    fun setNumberProvider(value: Pair<String, Boolean>) {
        _numberProvider.postValue(value)
    }

    fun getProductPulsa(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _productPulsa.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productPulsa.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productPulsa.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productPulsa.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _productPulsa.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }


    fun getProductData(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _productPaketData.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productPaketData.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productPaketData.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productPaketData.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _productPaketData.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getProductTelp(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _productPaketTelp.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productPaketTelp.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productPaketTelp.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productPaketTelp.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _productPaketTelp.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getProductSms(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _productSms.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productSms.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productSms.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productSms.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _productSms.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}