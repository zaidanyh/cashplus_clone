package com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.OrderCheckTokoRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.OrderCheckTokoResponse
import com.pasukanlangit.cashplus.model.response.TrackingOrderResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import kotlinx.coroutines.launch
import okio.IOException

class DetailHistoryTokoViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _detail = MutableLiveData<MyResponse<OrderCheckTokoResponse>>()
    val detail: LiveData<MyResponse<OrderCheckTokoResponse>> = _detail

    private val _tracking = MutableLiveData<MyResponse<TrackingOrderResponse>>()
    val tracking: LiveData<MyResponse<TrackingOrderResponse>> = _tracking

    private val _trxDone = MutableLiveData<MyResponse<SingleEvent<ErrorMessageResponse>>>()
    val trxDone: LiveData<MyResponse<SingleEvent<ErrorMessageResponse>>> = _trxDone

    fun getDetail(orderCheckTokoRequest: OrderCheckTokoRequest, accessToken: String) = viewModelScope.launch {
        _detail.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.orderCheckTokoOnline(orderCheckTokoRequest, accessToken)

            if(response.code() == 200){
                _detail.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _detail.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _detail.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _detail.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun tracking(orderCheckTokoRequest: OrderCheckTokoRequest, accessToken: String) = viewModelScope.launch {
        _tracking.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.orderTrackingTokoOnline(orderCheckTokoRequest, accessToken)

            if(response.code() == 200){
                _tracking.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _tracking.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _tracking.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _tracking.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }


    fun setTrxDone(orderCheckTokoRequest: OrderCheckTokoRequest, accessToken: String) = viewModelScope.launch {
        _trxDone.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.setTrxTOReceived(orderCheckTokoRequest, accessToken)

            if(response.code() == 200){
                response.body()?.let { body ->
                    _trxDone.postValue(MyResponse.success(SingleEvent(body)))
                }
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _trxDone.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _trxDone.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _trxDone.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }


}