package com.pasukanlangit.cashplus.ui.pages.others.settings.password

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
import com.pasukanlangit.cashplus.model.request_body.ChangePasswordRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class ChangePassViewModel(private val mainRepository: MainRepository, private val context: Context) : ViewModel() {
    private val _pass = MutableLiveData<MyResponse<UserModel>>()
    val pass: LiveData<MyResponse<UserModel>> = _pass

    fun changePassword(request: ChangePasswordRequest, token: String){
        viewModelScope.launch(Dispatchers.IO) {
            _pass.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.changePassword(request, token)

                if(response.code() == 200){
                    _pass.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        var message: String= errorBody.msg ?: errorBody.message ?: throw IOException()
                        if(errorBody.rc == "30") message = "Password Lama yang anda masukkan salah"
                        _pass.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _pass.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _pass.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}