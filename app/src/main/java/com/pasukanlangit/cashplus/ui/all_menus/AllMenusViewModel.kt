package com.pasukanlangit.cashplus.ui.all_menus

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

class AllMenusViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {

    private val _menuGames = MutableLiveData<MyResponse<ProductResultModel>>()
    val menuGames: LiveData<MyResponse<ProductResultModel>> = _menuGames

    private val _menuEmoney = MutableLiveData<MyResponse<ProductResultModel>>()
    val menuEmoney: LiveData<MyResponse<ProductResultModel>> = _menuEmoney

    fun getMenuGame(productRequest: ProductRequest, accessToken: String){
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
                        val  message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _menuGames.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getMenuEmoney(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _menuEmoney.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _menuEmoney.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val  message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _menuEmoney.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _menuEmoney.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _menuEmoney.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}