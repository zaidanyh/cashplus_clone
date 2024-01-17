package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.response.TransactionResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class TransactionViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _responseTransaction = MutableLiveData<SingleEvent<MyResponse<TransactionResponse>>>()
    val responseTransaction: LiveData<SingleEvent<MyResponse<TransactionResponse>>> = _responseTransaction

    fun sendTransaction(transactionRequest: TransactionRequest, token : String){
        viewModelScope.launch(Dispatchers.IO)  {
            _responseTransaction.postValue(SingleEvent(MyResponse.loading(null)))

            try{
                val response = mainRepository.sendTransactionPrePaid(transactionRequest, token)

                if(response.code() == 200){
                    _responseTransaction.postValue(SingleEvent(MyResponse.success(response.body())))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val trxResponse =
                            gson.fromJson(response.errorBody()!!.string(), TransactionResponse::class.java)
//
//                            when (trxResponse.rc) {
//                                "68" -> {
//                                    _responseTransaction.postValue(MyResponse.error("pending", trxResponse))
//                                }
//                                "63" -> {
//                                    _responseTransaction.postValue(MyResponse.error("Anda tidak diperkenankan melakukan transaksi ini, silahkan hubungi costumer service kami", null))
//                                }
//                                else -> {
//                                    _responseTransaction.postValue(MyResponse.error(trxResponse.msg, trxResponse))
//                                }
//                            }
                        _responseTransaction.postValue(SingleEvent(MyResponse.error(trxResponse.msg ?: "", trxResponse)))
                    } catch (e: IOException) {
                        _responseTransaction.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_gson), null)))
                    }

                }
            }catch (ex : Exception){
                _responseTransaction.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_network), null)))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _responseTransaction.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }


}