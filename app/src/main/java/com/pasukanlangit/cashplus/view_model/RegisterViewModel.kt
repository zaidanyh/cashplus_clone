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
import com.pasukanlangit.cashplus.domain.model.OtpResponse
import com.pasukanlangit.cashplus.domain.model.RegisterRequest
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.request_body.ChangePinRequest
import com.pasukanlangit.cashplus.model.request_body.ResetPwCodeRequest
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import java.util.ArrayDeque
import java.util.Queue

class RegisterViewModel(
    private val networkConnectivity: NetworkConnectivity,
    private val mainRepository: MainRepository,
    private val useCase: AppUseCases,
    private val mContext: Context
) : ViewModel() {
    private val _stateFirst = MutableStateFlow(false)
    val stateFirst: StateFlow<Boolean> = _stateFirst

    private val _stateSecond = MutableStateFlow(false)
    val stateSecond: StateFlow<Boolean> = _stateSecond

    private val _stateThird = MutableStateFlow(false)
    val stateThird: StateFlow<Boolean> = _stateThird

    private val _registerLoading = MutableStateFlow(false)
    val registerLoading: StateFlow<Boolean> get() =  _registerLoading
    private val _register = Channel<OtpResponse?>()
    val register get() =  _register.receiveAsFlow()
    private val _registerError = MutableStateFlow(ArrayDeque<String>())
    val registerError: StateFlow<Queue<String>> get() =  _registerError

    private val _otpRegisterResponse = MutableLiveData<MyResponse<UserModel>>()
    val otpRegisterResponse: LiveData<MyResponse<UserModel>> = _otpRegisterResponse

    private val _createPIN = MutableLiveData<MyResponse<UserModel>>()
    val createPIN: LiveData<MyResponse<UserModel>> = _createPIN

    fun setStateFirst(value: Boolean) {
        _stateFirst.value = value
    }

    fun setStateSecond(value: Boolean) {
        _stateSecond.value = value
    }

    fun setStateThird(value: Boolean) {
        _stateThird.value = value
    }

    fun register(registerRequest: RegisterRequest) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .registerUseCase(registerRequest)
                        .onEach {
                            it.data?.let { data -> _register.send(data) }
                            it.message?.let { error -> appendRegisterError(error) }
                            _registerLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendRegisterError(mContext.getString(R.string.exception_network))
            }
        })
    }

    fun registerOtpCode(resetPwCode: ResetPwCodeRequest) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _otpRegisterResponse.postValue(MyResponse.loading(null))

                        try{
                            val response = mainRepository.registerOtpCode(resetPwCode)

                            if(response.code() == 200){
                                _otpRegisterResponse.postValue(MyResponse.success(response.body()))
                            }else{
                                val gson: Gson = GsonBuilder().create()
                                try {
                                    val errorBody =
                                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                                    var message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                                    if(message.contains("terdaftar",true))
                                        message = "Nomor yang kamu gunakan sudah terdaftar"

                                    if (message.contains("wrong or expired"))
                                        message = "Kode OTP Register tidak valid"

                                    _otpRegisterResponse.postValue(MyResponse.error(message, null))
                                } catch (e: IOException) {
                                    _otpRegisterResponse.postValue(MyResponse.error(
                                        e.message ?: mContext.getString(R.string.exception_gson),
                                        null)
                                    )
                                }

                            }
                        }catch (ex : Exception) {
                            _otpRegisterResponse.postValue(MyResponse.error(ex.message ?: Constants.unknownError, null))
                        }
                    }
                } else
                    _otpRegisterResponse.postValue(MyResponse.error(mContext.getString(R.string.exception_network), null))
            }
        })
    }

    fun createPIN(changePinRequest: ChangePinRequest, token: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _createPIN.postValue(MyResponse.loading(null))

                        try{
                            val response = mainRepository.changePin(changePinRequest, token)

                            if(response.isSuccessful){
                                _createPIN.postValue(MyResponse.success(response.body()))
                            }else{
                                val gson: Gson = GsonBuilder().create()
                                try {
                                    val errorBody =
                                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                                    _createPIN.postValue(MyResponse.error(message, null))
                                } catch (e: IOException) {
                                    _createPIN.postValue(MyResponse.error(
                                        e.message ?: mContext.getString(R.string.exception_gson),
                                        null)
                                    )
                                }
                            }
                        }catch (ex : Exception){
                            _createPIN.postValue(MyResponse.error(ex.message ?: Constants.unknownError, null))
                        }
                    }
                } else
                    _createPIN.postValue(MyResponse.error(mContext.getString(R.string.exception_network), null))
            }
        })
    }

    private fun appendRegisterError(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _registerError.value = newMessage
    }

    fun removeRegisterError() {
        val currentMessage = registerError.value
        currentMessage.remove()
        _registerError.value = ArrayDeque(emptyList())
    }
}