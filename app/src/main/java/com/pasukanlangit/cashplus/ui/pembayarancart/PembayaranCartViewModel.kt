package com.pasukanlangit.cashplus.ui.pembayarancart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.CourierPriceRequest
import com.pasukanlangit.cashplus.model.request_body.OnlineStoreOrderRequest
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class PembayaranCartViewModel(private val mainRepository: MainRepository, private var sharedPref: com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource, private val context: Context): ViewModel() {
    private val _mainBookAddress = MutableLiveData<MyResponse<AddressBookData>>()
    val addressMainBookAddress: LiveData<MyResponse<AddressBookData>> = _mainBookAddress

    private val _subdistrictId = MutableLiveData<String?>()

    private val _weight = MutableLiveData<Int>()

    private val _allRequirementCompleted = MutableLiveData<Boolean>()
    val allRequirementCompleted : LiveData<Boolean> = _allRequirementCompleted

    private val _courierChoosen = MutableLiveData<CostsItem?>()
    val courierChoosen: LiveData<CostsItem?> = _courierChoosen

    private val _courierPrice = MutableLiveData<MyResponse<SingleEvent<CourierPriceResponse>>>()
    val courierPrice: LiveData<MyResponse<SingleEvent<CourierPriceResponse>>> = _courierPrice

    private val _order = MutableLiveData<MyResponse<SingleEvent<OnlineStoreOrderResponse>>>()
    val order: LiveData<MyResponse<SingleEvent<OnlineStoreOrderResponse>>> = _order

    private val _qtyProduct = MutableLiveData(1)
    val qtyProduct : LiveData<Int> = _qtyProduct

    fun setQtyProduct(value: Int) {
        _qtyProduct.value = value
    }


//    fun getMainBookAddress(uuid: String, accessToken: String){
//        viewModelScope.launch(Dispatchers.IO)  {
//            _mainBookAddress.postValue(MyResponse.loading(null))
//
//            try{
//                val response = mainRepository.getListAddressBook(LoginRequest(uuid,""), accessToken)
//
//                if(response.code() == 200){
//                    mainRepository.getSavedAddressBook(sharedPref.getPhoneNumber() ?: "-1").collect { addressFromDb ->
//                        if(response.body()?.data != null && !response.body()?.data.isNullOrEmpty()) {
//                            var addressBookChoosen = response.body()?.data?.singleOrNull { it.bookAddressId == addressFromDb?.bookAddressId }
//                            if(addressBookChoosen == null) addressBookChoosen = response.body()?.data?.singleOrNull { it.isMainAddress == "1" }
//
//                            _subdistrictId.postValue(addressBookChoosen?.subdistrictId)
//                            _mainBookAddress.postValue(MyResponse.success(addressBookChoosen))
//                        }else{
//                            _subdistrictId.postValue(null)
//                            _mainBookAddress.postValue(MyResponse.success(null))
//                        }
//                        _courierChoosen.postValue(null)
//                        checkAllRequirement()
//                    }
//                }else{
//                    val gson: Gson = GsonBuilder().create()
//                    try {
//                        val errorBody =
//                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
//                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()
//
//                        _mainBookAddress.postValue(MyResponse.error(message, null))
//                    } catch (e: IOException) {
//                        _mainBookAddress.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
//                    }
//
//                }
//            }catch (ex : Exception){
//                _mainBookAddress.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
//            }
//        }
//    }
    fun getMainBookAddress(uuid: String, accessToken: String){
        viewModelScope.launch(Dispatchers.IO) {
            _mainBookAddress.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getMainOrderBook(ProfileRequest(uuid), accessToken)

                if(response.code() == 200){
                    response.body()?.let {body ->
                        _mainBookAddress.postValue(MyResponse.success(body.data))
                        _subdistrictId.postValue(body.data?.subdistrictId)
                    }

                    _courierChoosen.postValue(null)
                    checkAllRequirement()
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _mainBookAddress.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _mainBookAddress.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _mainBookAddress.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    private fun checkAllRequirement(){
        val valueCourier = courierChoosen.value
        val valueAddress = addressMainBookAddress.value?.data
        _allRequirementCompleted.postValue(valueCourier != null && valueAddress != null)
    }


    fun setWeight(weight: Int){
        _weight.value = weight
    }

    fun setCourierChoosen(courier: CostsItem){
        _courierChoosen.value = courier
        checkAllRequirement()
    }

    fun getCourierPrice(couerierKey: String, uuid: String, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _courierPrice.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getPriceShipment(
                    CourierPriceRequest(
                        couerierKey,
                        _subdistrictId.value ?: "",
                        _weight.value.toString(),
                        uuid
                    ), accessToken)

                if(response.code() == 200){
                    response.body()?.let {body->
                        _courierPrice.postValue(MyResponse.success(SingleEvent(body)))
                    }
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _courierPrice.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _courierPrice.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _courierPrice.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun order(orderRequest: OnlineStoreOrderRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _order.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.orderOnlineStore(orderRequest, accessToken)

                if(response.code() == 200){
                    response.body()?.let {body->
                        _order.postValue(MyResponse.success(SingleEvent(body)))
                    }
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _order.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _order.postValue(MyResponse.error(context.getString(R.string.exception_gson),null))
                    }

                }
            }catch (ex : Exception){
                _order.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}