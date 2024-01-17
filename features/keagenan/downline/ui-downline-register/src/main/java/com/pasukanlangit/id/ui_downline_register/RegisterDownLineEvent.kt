package com.pasukanlangit.id.ui_downline_register

sealed class RegisterDownLineEvent {
    object GetProvinces: RegisterDownLineEvent()
    data class GetCity(val provinceId: String): RegisterDownLineEvent()
    data class GetDistrict(val cityId: String): RegisterDownLineEvent()
    data class GetVillage(val districtId: String): RegisterDownLineEvent()
    data class RegisterDownLine(
        val phone: String, val fullName: String, val ownerName: String, val password: String,
        val email: String, val address: String, val prov: String, val city: String, val district: String,
        val village: String, val zipcode: String
    ): RegisterDownLineEvent()
    object RemoveHeadMessage: RegisterDownLineEvent()
}
