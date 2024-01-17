package com.pasukanlangit.cashplus.ui.addressbook.add

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import kotlinx.coroutines.launch
import okio.IOException

class AddUpdateAddressBookViewModel(private val mainRepository: MainRepository,private val context: Context) : ViewModel() {

    private val _province = MutableLiveData<MyResponse<ProvinceShipmentResponse>>()
    val province: LiveData<MyResponse<ProvinceShipmentResponse>> = _province

    private val _city = MutableLiveData<MyResponse<CityShipmentResponse>?>()
    val city: LiveData<MyResponse<CityShipmentResponse>?> = _city

    private val _subdistrict = MutableLiveData<MyResponse<SubdistrictShipmentResponse>?>()
    val subdistrict: LiveData<MyResponse<SubdistrictShipmentResponse>?> = _subdistrict

    private val _addressAddedOrUpdated = MutableLiveData<MyResponse<UserModel>>()
    val addressAddedOrUpdated: LiveData<MyResponse<UserModel>> = _addressAddedOrUpdated

    fun getProvinces(uuid: String, token: String)= viewModelScope.launch {
        _province.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.getProvinceShipment(ProfileRequest(uuid), token)

            if(response.code() == 200){
                _province.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _province.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _province.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
                }

            }
        }catch (ex : Exception){
            _province.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun getCity(cityShipmentRequest: CityShipmentRequest, token: String)= viewModelScope.launch {
        _city.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.getCityShipment(cityShipmentRequest, token)

            if(response.code() == 200){
                _city.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _city.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _city.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _city.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun resetCity(){
        _city.value = null
    }

    fun resetSubdistrict(){
        _subdistrict.value = null
    }

    fun getSubDistrict(subdistrictShipmentRequest: SubdistrictShipmentRequest, token: String)= viewModelScope.launch {
        _subdistrict.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.getSubDistrictShipment(subdistrictShipmentRequest, token)

            if(response.code() == 200){
                _subdistrict.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _subdistrict.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _subdistrict.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _subdistrict.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun addAddressBook(addAddressBookRequest: AddAddressBookRequest, token: String, isUpdate : Boolean = false)= viewModelScope.launch {
        _addressAddedOrUpdated.postValue(MyResponse.loading(null))

        try{
            val response = if(isUpdate){
                mainRepository.updateAddressShipment(addAddressBookRequest, token)
            }else{
                mainRepository.addAddressShipment(addAddressBookRequest, token)
            }

            if(response.code() == 200){
                _addressAddedOrUpdated.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _addressAddedOrUpdated.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _addressAddedOrUpdated.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _addressAddedOrUpdated.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }
}