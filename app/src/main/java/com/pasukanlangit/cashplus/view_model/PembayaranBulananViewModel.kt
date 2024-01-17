package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

class PembayaranBulananViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _tagihanPascaBayar = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanPascaBayar : LiveData<MyResponse<ProductResultModel>> = _tagihanPascaBayar

    private val _tagihanPbb = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanPbb : LiveData<MyResponse<ProductResultModel>> = _tagihanPbb

    private val _tagihanPDAM = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanPDAM : LiveData<MyResponse<ProductResultModel>> = _tagihanPDAM

    private val _tagihanTvKabel = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanTvKabel : LiveData<MyResponse<ProductResultModel>> = _tagihanTvKabel

    private val _tagihanSamsat = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanSamsat : LiveData<MyResponse<ProductResultModel>> = _tagihanSamsat

    private val _tagihanFinance = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanFinance : LiveData<MyResponse<ProductResultModel>> = _tagihanFinance

    private val _tagihanCredit = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanCredit : LiveData<MyResponse<ProductResultModel>> = _tagihanCredit

    private val _tagihanTelkom = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanTelkom : LiveData<MyResponse<ProductResultModel>> = _tagihanTelkom

    private val _tagihanAsuransi = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanAsuransi : LiveData<MyResponse<ProductResultModel>> = _tagihanAsuransi

    private val _tagihanPertagas = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanPertagas : LiveData<MyResponse<ProductResultModel>> = _tagihanPertagas

    private val _tagihanGasNegara = MutableLiveData<MyResponse<ProductResultModel>>()
    val tagihanGasNegara : LiveData<MyResponse<ProductResultModel>> = _tagihanGasNegara

    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber: StateFlow<String?> = _phoneNumber

    private val _billTrx = MutableLiveData<SingleEvent<MyResponse<TransactionTAGResponse?>>>()
    val billTrx: LiveData<SingleEvent<MyResponse<TransactionTAGResponse?>>> = _billTrx

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun getProductPascaBayar(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanPascaBayar.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanPascaBayar.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanPascaBayar.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanPascaBayar.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanPascaBayar.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanPascaBayar.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductPbb(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanPbb.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanPbb.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanPbb.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanPbb.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanPbb.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }

//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanPbb.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductPDAM(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanPDAM.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanPDAM.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    val productModel : ProductResultModel
                    try {
                        productModel =
                            gson.fromJson(response.errorBody()!!.string(), ProductResultModel::class.java)

                        _tagihanPDAM.postValue(MyResponse.error(productModel.msg, null))
                    } catch (e: IOException) {
                        _tagihanPDAM.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _tagihanPDAM.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanPDAM.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductTvKabel(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanTvKabel.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanTvKabel.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanTvKabel.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanTvKabel.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanTvKabel.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanTvKabel.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductSamsat(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanSamsat.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanSamsat.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanSamsat.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanSamsat.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanSamsat.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanSamsat.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductFinance(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanFinance.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanFinance.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanFinance.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanFinance.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanFinance.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanFinance.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductCredit(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanCredit.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanCredit.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanCredit.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanCredit.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanCredit.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanCredit.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }


    fun getProductTelkom(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanTelkom.postValue(MyResponse.loading(null))
            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanTelkom.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanTelkom.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanTelkom.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanTelkom.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanTelkom.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductAsuransi(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanAsuransi.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanAsuransi.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanAsuransi.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanAsuransi.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanAsuransi.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanAsuransi.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun getProductPertagas(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanPertagas.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanPertagas.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanPertagas.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanPertagas.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanPertagas.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanPertagas.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }


    fun getProductGasNegara(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _tagihanGasNegara.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _tagihanGasNegara.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _tagihanGasNegara.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _tagihanGasNegara.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }
            }catch (ex : Exception){
                _tagihanGasNegara.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
//            if(MyUtils.isOnline()){
//
//            }else{
//                _tagihanGasNegara.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
//            }
        }
    }

    fun payBill(transactionRequest: TransactionRequest, accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _billTrx.postValue(SingleEvent(MyResponse.loading(null)))
            try {
                val response = mainRepository.sendTransactionTAG(transactionRequest, accessToken)
                if (response.isSuccessful) {
                    _billTrx.postValue(SingleEvent(MyResponse.success(response.body())))
                } else {
                    val gson: Gson = GsonBuilder().create()
                    val productModel: ProductResultModel
                    try {
                        productModel =
                            gson.fromJson(response.errorBody()?.string(), ProductResultModel::class.java)
                        _billTrx.postValue(SingleEvent(MyResponse.error(productModel.msg, null)))
                    } catch (e: IOException) {
                        _billTrx.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_gson), null)))
                    }
                }
            } catch (e: Exception) {
                _billTrx.postValue(SingleEvent(MyResponse.error(context.getString(R.string.exception_network), null)))
            }
        }
    }
}