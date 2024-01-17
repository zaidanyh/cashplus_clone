package com.pasukanlangit.id.ui_cashgold_address.addupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.LocationList
import com.pasukanlangit.id.model.VillageList
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class AddUpdateAddressViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){
//    private val _stateLoading = MutableStateFlow(false)
//    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _stateProvinces = MutableStateFlow(listOf<LocationList>())
    val stateProvinces: StateFlow<List<LocationList>> = _stateProvinces

    private val _stateCity = MutableStateFlow(listOf<LocationList>())
    val stateCity: StateFlow<List<LocationList>> = _stateCity

    private val _stateDistrict = MutableStateFlow(listOf<LocationList>())
    val stateDistrict: StateFlow<List<LocationList>> = _stateDistrict

    private val _stateVillage = MutableStateFlow(listOf<VillageList>())
    val stateVillage: StateFlow<List<VillageList>> = _stateVillage

    private val _stateActionSuccess = MutableStateFlow<Boolean?>(null)
    val stateActionSuccess: StateFlow<Boolean?> = _stateActionSuccess

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    init {
        onTriggerEvent(AddUpdateAddressEvent.GetProvinces)
    }

    fun onTriggerEvent(event: AddUpdateAddressEvent){
        when(event){
            is AddUpdateAddressEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is AddUpdateAddressEvent.GetCity -> {
                getCity(event.provinces)
            }
            is AddUpdateAddressEvent.GetDistrict -> {
                getDistrict(event.city)
            }
            is AddUpdateAddressEvent.GetProvinces -> {
                getProvinces()
            }
            is AddUpdateAddressEvent.GetVillage -> {
                getVillage(event.city, event.district)
            }
            is AddUpdateAddressEvent.AddAddress -> {
                addAddress(
                    event.address,
                    event.city,
                    event.district,
                    event.province,
                    event.village,
                    event.zipCode
                )
            }
            is AddUpdateAddressEvent.UpdateAddress -> {
                updateAddress(
                    event.id,
                    event.address,
                    event.city,
                    event.district,
                    event.province,
                    event.village,
                    event.zipCode,
                    event.isMain
                )
            }
        }
    }

    private fun updateAddress(id: Int?, address: String, city: String, district: String, province: String, village: String, zipCode: String, main: Boolean?) {
        useCases
            .updateAddress(
                address = address,
                city = city,
                district = district,
                province = province,
                village = village,
                zipCode = zipCode,
                isMain = main,
                id = id
            )
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { updateStatus ->
                    _stateActionSuccess.value = updateStatus
                }

                it.message?.let { message ->
                    appendErrorMessage(message)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addAddress(fullAddress: String, city: String, district: String, province: String, village: String, zipCode: String) {
        _stateActionSuccess.value = null
        useCases
            .addAddress(
                address = fullAddress,
                city = city,
                district = district,
                province = province,
                village = village,
                zipCode = zipCode
            )
            .onEach {
                _stateLoadingButton.value = it.isLoading
                it.data?.let { isSuccess ->
                    _stateActionSuccess.value = isSuccess
                }

                it.message?.let { message ->
                    appendErrorMessage(message)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getVillage(city: String, district: String) {
        useCases
            .getVillage(
                city = city,
                district = district
            )
            .onEach {
                it.data?.let { village ->
                    _stateVillage.value = village
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getDistrict(city: String) {
        useCases
            .getDistrict(city)
            .onEach {
                it.data?.let { district ->
                    _stateDistrict.value = district
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getCity(province: String) {
        useCases
            .getCity(province)
            .onEach {
                it.data?.let { cities ->
                    _stateCity.value = cities
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getProvinces() {
        useCases
            .getProvinces()
            .onEach {
                it.data?.let { provinces ->
                    _stateProvinces.value = provinces
                }
            }
            .launchIn(viewModelScope)
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }
}