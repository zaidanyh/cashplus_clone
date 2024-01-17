package com.pasukanlangit.cashplus.ui.pembayarancart.pay

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class EnterPinOnlineStoreViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {
    private val _responseTransaction = MutableLiveData<MyResponse<TransactionPayResponse>>()
    val responseTransaction: LiveData<MyResponse<TransactionPayResponse>> = _responseTransaction

    fun deleteCartSelectedFromDB(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteCheckedProductFromCart(userId)
    }

    fun sendTransaction(transactionRequest: TransactionRequest, token : String){
        viewModelScope.launch(Dispatchers.IO)  {
            _responseTransaction.postValue(MyResponse.loading(null))

            try{
                val productCode = transactionRequest.kode_produk
                val response = mainRepository.sendTransactionPostPaid(transactionRequest.copy(kode_produk = "TAG$productCode"), token)

                if(response.code() == 200){
                    if(response.body()?.rc == "00"){
                        payTransaction(transactionRequest.copy(kode_produk = "PAY$productCode"), token)
                    }else{
                        _responseTransaction.postValue(MyResponse.error(response.body()?.msg ?: "Terdapat kesalahan, silahkan coba lagi", null))
                    }
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val trxResponse =
                            gson.fromJson(response.errorBody()!!.string(), TransactionPayResponse::class.java)
                        _responseTransaction.postValue(MyResponse.error(trxResponse.msg ?: context.getString(R.string.exception_gson), trxResponse))
                    } catch (e: IOException) {
                        _responseTransaction.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _responseTransaction.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    private fun payTransaction(trxRequest: TransactionRequest, token: String) = viewModelScope.launch {
        try{
            val responsePay = mainRepository.sendTransactionPostPaid(trxRequest,token)

            if(responsePay.code() == 200){
                _responseTransaction.postValue(MyResponse.success(responsePay.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val trxResponse =
                        gson.fromJson(responsePay.errorBody()!!.string(), TransactionPayResponse::class.java)
                    _responseTransaction.postValue(MyResponse.error(trxResponse.msg ?: context.getString(R.string.exception_gson), trxResponse))
                } catch (e: IOException) {
                    _responseTransaction.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _responseTransaction.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }

    }
}