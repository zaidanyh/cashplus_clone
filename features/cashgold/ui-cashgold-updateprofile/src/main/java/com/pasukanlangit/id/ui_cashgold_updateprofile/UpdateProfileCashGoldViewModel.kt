package com.pasukanlangit.id.ui_cashgold_updateprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.LocationList
import com.pasukanlangit.id.model.UserCashGold
import com.pasukanlangit.id.model.VillageList
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class UpdateProfileCashGoldViewModel(
    private val useCase: CashGoldUseCase
): ViewModel(){
    private val _stateLoadingButton = MutableStateFlow(false)
    val stateLoadingButton: StateFlow<Boolean> = _stateLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateIsSaved = MutableStateFlow<Boolean?>(null)
    val stateIsSaved: StateFlow<Boolean?> = _stateIsSaved

    private val _stateProvinces = MutableStateFlow(listOf<LocationList>())
    val stateProvinces: StateFlow<List<LocationList>> = _stateProvinces

    private val _stateCity = MutableStateFlow(listOf<LocationList>())
    val stateCity: StateFlow<List<LocationList>> = _stateCity

    private val _stateDistrict = MutableStateFlow(listOf<LocationList>())
    val stateDistrict: StateFlow<List<LocationList>> = _stateDistrict

    private val _stateVillage = MutableStateFlow(listOf<VillageList>())
    val stateVillage: StateFlow<List<VillageList>> = _stateVillage

    private val _profile = MutableStateFlow<UserCashGold?>(null)
    val profile: StateFlow<UserCashGold?> = _profile

    init {
        onTriggerEvent(UpdateProfileCashGoldEvent.GetProfileGold)
    }

    fun onTriggerEvent(event: UpdateProfileCashGoldEvent){
        when(event){
            is UpdateProfileCashGoldEvent.GetProfileGold -> {
                getProfile()
            }
            is UpdateProfileCashGoldEvent.SaveProfile -> {
                updateProfile(
                    name = event.user,
                    ktpNumber = event.identityNumber,
                    gender = event.gender,
                    marriageStatus = event.maritalStatus,
                    birthPlace = event.birthPlace,
                    birthDate = event.dayOfBirth,
                    email = event.email,
                    phone = event.phone,
                    religion = event.religion,
                    npwpNumber = event.taxIdentityNumber,
                    lastEducation = event.lastEducation,
                    profession = event.profession,
                    incomeSource = event.incomeSource,
                    incomePerYear = event.incomePerYear,
                    province = event.province,
                    city = event.city,
                    district = event.district,
                    village = event.village,
                    address = event.address,
                    zipCode = event.zipCode
                )
            }
            is UpdateProfileCashGoldEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is UpdateProfileCashGoldEvent.GetProvinces -> {
                getProvinces()
            }
            is UpdateProfileCashGoldEvent.GetCity -> {
                getCity(event.provinces)
            }
            is UpdateProfileCashGoldEvent.GetDistrict -> {
                getDistrict(event.city)
            }
            is UpdateProfileCashGoldEvent.GetVillage -> {
                getVillage(event.city, event.district)
            }
        }
    }

    private fun getProvinces() {
        useCase
            .getProvinces()
            .onEach {
                it.data?.let { provinces ->
                    _stateProvinces.value = provinces
                }
            }
            .launchIn(viewModelScope)
    }


    private fun getCity(province: String) {
        useCase
            .getCity(province)
            .onEach {
                it.data?.let { cities ->
                    _stateCity.value = cities
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getDistrict(city: String) {
        useCase
            .getDistrict(city)
            .onEach {
                it.data?.let { district ->
                    _stateDistrict.value = district
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getVillage(city: String, district: String) {
        useCase
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

    private fun getProfile() {
        useCase
            .getProfileCashGold()
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { profile ->
                    _profile.value = profile
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
            }
            .launchIn(viewModelScope)
    }



    private fun updateProfile(
        name: String?,
        ktpNumber: String,
        gender: String?,
        marriageStatus: String?,
        birthPlace: String?,
        birthDate: String?,
        email: String,
        phone: String,
        religion: String?,
        npwpNumber: String?,
        lastEducation: String?,
        profession: String?,
        incomeSource: String?,
        incomePerYear: String?,
        province: String?,
        city: String?,
        district: String?,
        village: String?,
        address: String?,
        zipCode: String?
    ){
        _stateIsSaved.value = null
        useCase
            .updateUserCashGold(
                user = name,
                identityNumber = ktpNumber,
                gender = gender,
                maritalStatus = marriageStatus,
                birthPlace = birthPlace,
                dayOfBirth = birthDate,
                email = email,
                phone = phone,
                religion = religion,
                taxIdentityNumber = npwpNumber,
                lastEducation = lastEducation,
                profession = profession,
                incomeSource = incomeSource,
                incomePerYear = incomePerYear,
                province = province,
                city = city,
                district = district,
                village = village,
                address = address,
                zipCode = zipCode
            )
            .onEach {
                _stateLoadingButton.value = it.isLoading

                it.data?.let { isSaved ->
                    _stateIsSaved.value = isSaved
                    getProfile()
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
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