package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.utils.SingleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class TopUpPLNViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _topUpPln = MutableLiveData<MyResponse<ProductResultModel>>()
    val topUpPln : LiveData<MyResponse<ProductResultModel>> = _topUpPln

    private val _tokenInfoDetail = MutableLiveData<SingleEvent<MyResponse<TransactionTAGResponse>>>()
    val tokenInfoDetail : LiveData<SingleEvent<MyResponse<TransactionTAGResponse>>> = _tokenInfoDetail

    private val _topUpTagihanPln = MutableLiveData<SingleEvent<MyResponse<TransactionTAGResponse>>>()
    val topUpTagihanPln : LiveData<SingleEvent<MyResponse<TransactionTAGResponse>>> = _topUpTagihanPln

    fun getProductPln(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _topUpPln.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _topUpPln.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    val productModel : ProductResultModel
                    try {
                        productModel =
                            gson.fromJson(response.errorBody()!!.string(), ProductResultModel::class.java)

                        _topUpPln.postValue(MyResponse.error(productModel.msg, null))
                    } catch (e: IOException) {
                        _topUpPln.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _topUpPln.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _topUpPln.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductTagihanPln(transactionRequest: TransactionRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _topUpTagihanPln.postValue(SingleEvent(MyResponse.loading(null)))

            try{
                val response = mainRepository.sendTransactionTAG(transactionRequest, accessToken)

                if(response.code() == 200){
                    _topUpTagihanPln.postValue(SingleEvent(MyResponse.success(response.body())))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    val productModel : ProductResultModel
                    try {
                        productModel =
                            gson.fromJson(response.errorBody()!!.string(), ProductResultModel::class.java)

                        _topUpTagihanPln.postValue(SingleEvent(MyResponse.error(productModel.msg, null)))
                    } catch (e: IOException) {
                        _topUpTagihanPln.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_gson), null)))
                    }

                }
            }catch (ex : Exception){
                _topUpTagihanPln.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_network), null)))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _topUpTagihanPln.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getInfoDetailToken(transactionRequest: TransactionRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tokenInfoDetail.postValue(SingleEvent(MyResponse.loading(null)))

            try{
                val response = mainRepository.sendTransactionTAG(transactionRequest, accessToken)

                if(response.code() == 200){
                    _tokenInfoDetail.postValue(SingleEvent(MyResponse.success(response.body())))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    val productModel : ProductResultModel
                    try {
                        productModel =
                            gson.fromJson(response.errorBody()!!.string(), ProductResultModel::class.java)

                        _tokenInfoDetail.postValue(SingleEvent(MyResponse.error(productModel.msg, null)))
                    } catch (e: IOException) {
                        _tokenInfoDetail.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_gson), null)))
                    }

                }
            }catch (ex : Exception){
                _tokenInfoDetail.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_network), null)))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tokenInfoDetail.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }
}