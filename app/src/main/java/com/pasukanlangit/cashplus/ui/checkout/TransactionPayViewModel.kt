package com.pasukanlangit.cashplus.ui.checkout

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.TransactionPayResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class TransactionPayViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _responseTransaction = MutableLiveData<SingleEvent<MyResponse<TransactionPayResponse>>>()
    val responseTransaction: LiveData<SingleEvent<MyResponse<TransactionPayResponse>>> = _responseTransaction

    fun sendTransaction(transactionRequest: TransactionRequest, token : String){
        viewModelScope.launch(Dispatchers.IO)  {
            _responseTransaction.postValue(SingleEvent(MyResponse.loading(null)))

            try{
                val response = mainRepository.sendTransactionPostPaid(transactionRequest, token)

                if(response.code() == 200){
                    _responseTransaction.postValue(SingleEvent(MyResponse.success(response.body())))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val trxResponse =
                            gson.fromJson(response.errorBody()!!.string(), TransactionPayResponse::class.java)
                        _responseTransaction.postValue(SingleEvent(MyResponse.error(trxResponse.msg ?: context.getString(R.string.exception_gson), trxResponse)))
                    } catch (e: IOException) {
                        _responseTransaction.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_gson), null)))
                    }

                }
            }catch (ex : Exception){
                _responseTransaction.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_network), null)))
            }
        }
    }
}