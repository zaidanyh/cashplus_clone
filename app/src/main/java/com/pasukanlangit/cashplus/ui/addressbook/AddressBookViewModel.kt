package com.pasukanlangit.cashplus.ui.addressbook

import android.content.Context
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.AddAddressBookRequest
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.AddressBookData
import com.pasukanlangit.cashplus.model.response.AllAddressBookResponse
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.UserModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.cashplus.utils.SingleEvent
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class AddressBookViewModel(private val mainRepository: MainRepository, private val sharedPref : SharedPrefDataSource, private val context: Context): ViewModel() {
    private val _allAddressBook = MutableLiveData<MyResponse<AllAddressBookResponse>>()
    val allAddressBook: LiveData<MyResponse<AllAddressBookResponse>> = _allAddressBook

    private val _deletedAddressBook = MutableLiveData<MyResponse<SingleEvent<UserModel>>>()
    val deletedAddressBook: LiveData<MyResponse<SingleEvent<UserModel>>> = _deletedAddressBook

    private val _updateAddressBook = MutableLiveData<MyResponse<UserModel>>()
    val updateAddressBook: LiveData<MyResponse<UserModel>> = _updateAddressBook

    private val _savedAddressBook = MutableLiveData<AddressBookData?>()
    val savedAddressBook: LiveData<AddressBookData?> = _savedAddressBook

    fun getSavedAddressBook() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.getSavedAddressBook(sharedPref.getPhoneNumber() ?: "-1").collect {
            _savedAddressBook.postValue(it)
        }
    }

    fun getAllAddressBook(uuid: String, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _allAddressBook.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.getListAddressBook(ProfileRequest(uuid), accessToken)

            if(response.code() == 200){
                _allAddressBook.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _allAddressBook.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _allAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _allAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun deleteAddressBook(addAddressBookRequest: AddAddressBookRequest, token: String) = viewModelScope.launch {
        _deletedAddressBook.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.deleteAddressShipment(addAddressBookRequest, token)

            if(response.code() == 200){
                response.body()?.let { body ->
                    _deletedAddressBook.postValue(MyResponse.success(SingleEvent(body)))
                    getAllAddressBook(addAddressBookRequest.uuid, token)
                }
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _deletedAddressBook.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _deletedAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _deletedAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

    fun updateAddressBook(addAddressBookRequest: AddAddressBookRequest, token: String) = viewModelScope.launch {
        _updateAddressBook.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.updateAddressShipment(addAddressBookRequest, token)


            if(response.code() == 200){
                _updateAddressBook.postValue(MyResponse.success(response.body()))
            }else{
                val gson: Gson = GsonBuilder().create()
                try {
                    val errorBody =
                        gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                    val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                    _updateAddressBook.postValue(MyResponse.error(message, null))
                } catch (e: IOException) {
                    _updateAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                }

            }
        }catch (ex : Exception){
            _updateAddressBook.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }

//    fun insertAddressBookToDb(addressBookData: AddressBookData) = viewModelScope.launch {
//        addressBookData.userId = sharedPref.getPhoneNumber() ?: "-1"
//        mainRepository.insertAddressBookToDb(addressBookData)
//    }
}