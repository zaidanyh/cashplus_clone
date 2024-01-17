package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.response.UserModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.ChangePinRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class OtherChangePinViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {
    private val _user = MutableLiveData<MyResponse<UserModel>>()
    val user: LiveData<MyResponse<UserModel>> = _user

    fun changePin(changePinRequest: ChangePinRequest, token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.changePin(changePinRequest, token)

                if(response.code() == 200){
                    _user.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        var message: String= errorBody.msg ?: errorBody.message ?: throw IOException()
                        if(errorBody.rc == "30") message = "PIN Lama yang anda masukkan salah"
                        _user.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _user.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _user.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _user.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }
}