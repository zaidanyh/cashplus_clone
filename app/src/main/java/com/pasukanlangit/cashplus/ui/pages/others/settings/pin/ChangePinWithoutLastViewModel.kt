package com.pasukanlangit.cashplus.ui.pages.others.settings.pin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.OtpResponse
import com.pasukanlangit.cashplus.domain.model.ResetPINRequest
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.ResetPinOtpCodeRequest
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

class ChangePinWithoutLastViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val mainRepository: MainRepository,
    private val context: Context
): ViewModel() {

    private val _resetPinLoading = MutableStateFlow(false)
    val resetPinLoading: StateFlow<Boolean> = _resetPinLoading
    private val _resetPIN = Channel<OtpResponse?>()
    val resetPIN = _resetPIN.receiveAsFlow()
    private val _resetPinError = MutableStateFlow(ArrayDeque<String>())
    val resetPinError: StateFlow<Queue<String>> = _resetPinError

    private val _resetPINOTP = MutableLiveData<MyResponse<UserModel>>()
    val resetPINOTP: LiveData<MyResponse<UserModel>> = _resetPINOTP

    fun resetPIN(resetPINRequest: ResetPINRequest, token: String){
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .resetPINUseCase(resetPINRequest, token)
                        .onEach {
                            it.data?.let { data -> _resetPIN.send(data) }
                            it.message?.let { error -> appendResetPinMessage(error) }
                            _resetPinLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendResetPinMessage(context.getString(R.string.exception_network))
            }
        })
    }


    fun sendOtpChangePin(changePinOtpCodeRequest: ResetPinOtpCodeRequest, token: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _resetPINOTP.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.sendOtpChangePIN(changePinOtpCodeRequest,token)

                if(response.code() == 200){
                    _resetPINOTP.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        var message: String= errorBody.msg ?: errorBody.message ?: throw java.io.IOException()

                        if (message.contains("wrong or expired"))
                            message = "Kode OTP reset PIN tidak valid"

                        _resetPINOTP.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _resetPINOTP.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            } catch (ex : Exception) {
                _resetPINOTP.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    private fun appendResetPinMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _resetPinError.value = newMessage
    }

    fun removeResetPINError() {
        val currentMessage = resetPinError.value
        currentMessage.remove()
        _resetPinError.value = ArrayDeque(emptyList())
    }
}