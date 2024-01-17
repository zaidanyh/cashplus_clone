package com.pasukanlangit.cashplus.ui.checkout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class PembayaranTagihanViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _balance = MutableLiveData<MyResponse<BalanceResponseCore>>()
    val balance: LiveData<MyResponse<BalanceResponseCore>> = _balance

    fun getBalance(request: ProfileRequest, token: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _balance.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getBalance(request, token)

                if(response.code() == 200) {
                    _balance.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _balance.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _balance.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _balance.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}