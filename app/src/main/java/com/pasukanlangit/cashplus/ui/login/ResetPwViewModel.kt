package com.pasukanlangit.cashplus.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.OtpResponse
import com.pasukanlangit.cashplus.domain.model.ResetPasswordRequest
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.ResetPwCodeRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.UserModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import java.util.*

class ResetPwViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val mainRepository: MainRepository,
    private val context: Context
): ViewModel() {
    private val _resetPassLoading = MutableStateFlow(false)
    val resetPassLoading = _resetPassLoading
    private val _resetPass = Channel<OtpResponse?>()
    val resetPass = _resetPass.receiveAsFlow()
    private val _resetPassError = MutableStateFlow(ArrayDeque<String>())
    val resetPassError: StateFlow<Queue<String>> = _resetPassError

    private val _resetPwCode = MutableLiveData<MyResponse<UserModel>>()
    val resetPwCode: LiveData<MyResponse<UserModel>> = _resetPwCode

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .resetPasswordUseCase(resetPasswordRequest)
                        .onEach {
                            it.data?.let { data -> _resetPass.send(data) }
                            it.message?.let { error -> appendResetPassMessage(error) }
                            _resetPassLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendResetPassMessage(context.getString(R.string.exception_network))
            }
        })
    }

    fun resetPasswordCode(resetPwCode: ResetPwCodeRequest){
        viewModelScope.launch(Dispatchers.IO)  {
            _resetPwCode.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.resetPasswordCode(resetPwCode)

                if(response.code() == 200){
                    _resetPwCode.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody=
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        var message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        if(errorBody.rc == "14"){
                            message = "Reset Password Code atau Nomer Hp tidak valid"
                        }

                        if (message.contains("wrong or expired"))
                            message = "Kode OTP reset kata sandi tidak valid"

                        _resetPwCode.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _resetPwCode.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _resetPwCode.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    private fun appendResetPassMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _resetPassError.value = newMessage
    }

    fun removeResetPassError() {
        val currentMessage = resetPassError.value
        currentMessage.remove()
        _resetPassError.value = ArrayDeque(emptyList())
    }
}