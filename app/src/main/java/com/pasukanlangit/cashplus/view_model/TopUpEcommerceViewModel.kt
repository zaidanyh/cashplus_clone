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

class TopUpEcommerceViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _numberEcommerce = MutableLiveData<String>()
    val numberEcommerce : LiveData<String> = _numberEcommerce

    private val _productEcommerce = MutableLiveData<MyResponse<ProductResultModel>>()
    val productEcommerce: LiveData<MyResponse<ProductResultModel>> = _productEcommerce


    fun setNumberEcommerce(value: String){
        _numberEcommerce.value = value
    }

    fun getProductEcommerce(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _productEcommerce.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _productEcommerce.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message = errorBody.msg ?: errorBody.message ?: throw IOException()

                        _productEcommerce.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _productEcommerce.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _productEcommerce.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _productEcommerce.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }
}