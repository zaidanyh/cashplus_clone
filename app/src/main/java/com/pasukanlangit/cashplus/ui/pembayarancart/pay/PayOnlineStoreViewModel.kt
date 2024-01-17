package com.pasukanlangit.cashplus.ui.pembayarancart.pay

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
import com.pasukanlangit.id.core.model.ProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class PayOnlineStoreViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _user = MutableLiveData<MyResponse<ProfileResponse>>()
    val user: LiveData<MyResponse<ProfileResponse>> = _user

    fun getProfile(profileRequest: ProfileRequest, token: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _user.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProfile(profileRequest, token)

                if(response.code() == 200){
                    _user.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _user.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _user.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _user.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}