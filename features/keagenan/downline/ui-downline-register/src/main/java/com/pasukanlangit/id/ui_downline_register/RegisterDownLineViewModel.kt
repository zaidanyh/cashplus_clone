package com.pasukanlangit.id.ui_downline_register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.domain_downline.model.LocationNameResponse
import com.pasukanlangit.id.domain_downline.model.MessageResponse
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class RegisterDownLineViewModel(
    private val useCases: DownLineUseCases
): ViewModel(){

    private val _provinces = MutableStateFlow<List<LocationNameResponse>?>(null)
    val provinces: StateFlow<List<LocationNameResponse>?> = _provinces

    private val _cities = MutableStateFlow<List<LocationNameResponse>?>(null)
    val cities: StateFlow<List<LocationNameResponse>?> = _cities

    private val _districts = MutableStateFlow<List<LocationNameResponse>?>(null)
    val districts: StateFlow<List<LocationNameResponse>?> = _districts

    private val _villages = MutableStateFlow<List<LocationNameResponse>?>(null)
    val villages: StateFlow<List<LocationNameResponse>?> = _villages

    private val _downlineRegister = MutableStateFlow<MessageResponse?>(null)
    val downlineRegister: StateFlow<MessageResponse?> = _downlineRegister

    private val _isLoadingButton = MutableStateFlow(false)
    val isLoadingButton: StateFlow<Boolean> = _isLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    init {
        onTriggerEvent(RegisterDownLineEvent.GetProvinces)
    }

    fun onTriggerEvent(event: RegisterDownLineEvent){
        when(event){
            is RegisterDownLineEvent.GetProvinces -> {
                getProvinces()
            }
            is RegisterDownLineEvent.RemoveHeadMessage -> {
                removeHeadQueue()
            }
            is RegisterDownLineEvent.GetCity -> {
                getCity(event.provinceId)
            }
            is RegisterDownLineEvent.GetDistrict -> {
                getDistrict(event.cityId)
            }
            is RegisterDownLineEvent.GetVillage -> {
                getVillage(event.districtId)
            }
            is RegisterDownLineEvent.RegisterDownLine -> {
                registerDownLine(
                    phone = event.phone, fullName = event.fullName, ownerName = event.ownerName,
                    email = event.email, password = event.password, address = event.address, prov = event.prov,
                    city = event.city, district = event.district, village = event.village, zipcode = event.zipcode
                )
            }
        }
    }

    private fun registerDownLine(
        phone: String, fullName: String, ownerName: String, email: String, password: String,
        address: String, prov: String, city: String, district: String, village: String, zipcode: String
    ) {
        useCases
            .registerDownLine(
                name = fullName, phoneNumber = phone, owner = ownerName, emailAddress = email, password = password,
                address = address, prov = prov, city = city, district = district, village = village,
                zipCode = zipcode
            )
            .onEach {
                it.data?.let { response ->
                    _downlineRegister.value = response
                    _downlineRegister.value = null
                }
                it.message?.let { error -> appendErrorMessage(error) }
                _isLoadingButton.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    private fun getVillage(districtId: String) {
        _villages.value = null
        useCases
            .getVillage(districtId = districtId)
            .onEach {
                it.data?.let { villages -> _villages.value = villages }
                it.message?.let { error -> appendErrorMessage(error) }
            }
            .launchIn(viewModelScope)
    }

    private fun getDistrict(cityId: String) {
        _districts.value = null
        _villages.value = null
        useCases
            .getDistrict(cityId = cityId)
            .onEach {
                it.data?.let { districts -> _districts.value = districts }
                it.message?.let { error -> appendErrorMessage(error) }
            }
            .launchIn(viewModelScope)
    }

    private fun getCity(provinceId: String) {
        _cities.value = null
        _districts.value = null
        _villages.value = null
        useCases
            .getCity(provinceId = provinceId)
            .onEach {
                it.data?.let { cities -> _cities.value = cities }
                it.message?.let { error -> appendErrorMessage(error) }
            }
            .launchIn(viewModelScope)
    }

    private fun getProvinces() {
        _provinces.value = null
        _cities.value = null
        _districts.value = null
        _villages.value = null
        useCases
            .getProvinces()
            .onEach {
                it.data?.let { provinces -> _provinces.value = provinces }
                it.message?.let { error -> appendErrorMessage(error) }
            }
            .launchIn(viewModelScope)
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = ArrayDeque(newMessage)
    }
}