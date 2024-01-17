package com.pasukanlangit.cashplus.view_model

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

class ChangeProfileViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _provinces = MutableLiveData<MyResponse<ProvinceResponse>>()
    val provinces: LiveData<MyResponse<ProvinceResponse>> = _provinces

    private val _districts = MutableLiveData<MyResponse<DistrictsResponse>?>()
    val districts: LiveData<MyResponse<DistrictsResponse>?> = _districts

    private val _subDistricts = MutableLiveData<MyResponse<SubdistrictsResponse>?>()
    val subDistricts: LiveData<MyResponse<SubdistrictsResponse>?> = _subDistricts

    private val _villages = MutableLiveData<MyResponse<VillageResponse>?>()
    val villages: LiveData<MyResponse<VillageResponse>?> = _villages

    private val _updateProfile = MutableLiveData<MyResponse<UserModel>>()
    val updateProfile: LiveData<MyResponse<UserModel>> = _updateProfile

    fun getProvinces(request: ProfileRequest) {
        viewModelScope.launch(Dispatchers.IO)  {
            _provinces.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProvinces(request)

                if(response.isSuccessful){
                    _provinces.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _provinces.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _provinces.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }

            }catch (ex : Exception){
                _provinces.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

//    fun setIsModeEditImage(value: Boolean?){
//        this._isModeEditImage.value = value
//    }

    fun getDistricts(districtRequest: DistrictRequest){
        viewModelScope.launch(Dispatchers.IO)  {
            _districts.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getDistricts(districtRequest)

                if(response.isSuccessful){
                    _districts.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _provinces.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _provinces.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }

            }catch (ex : Exception){
                _districts.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getSubDistricts(subdistrictRequest: SubdistrictRequest){
        viewModelScope.launch(Dispatchers.IO)  {
            _subDistricts.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getSubdistricts(subdistrictRequest)

                if(response.isSuccessful){
                    _subDistricts.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _subDistricts.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _subDistricts.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }

            }catch (ex : Exception){
                _subDistricts.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getVillage(villageRequest: VillageRequest){
        viewModelScope.launch(Dispatchers.IO)  {
            _villages.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getVillages(villageRequest)

                if(response.isSuccessful){
                    _villages.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _villages.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _villages.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }

            }catch (ex : Exception){
                _villages.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun updateProfile(updateProfileRequest: UpdateProfilRequest, token : String){
        viewModelScope.launch(Dispatchers.IO)  {
            _updateProfile.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.updateProfile(updateProfileRequest,token)

                if(response.code() == 200){
                    _updateProfile.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _updateProfile.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _updateProfile.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }
                }

            }catch (ex : Exception){
                _updateProfile.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun resetDistrict(){
        _districts.value = null
    }

    fun resetSubDistrict(){
        _subDistricts.value = null
    }

    fun resetVillages(){
        _villages.value = null
    }
}