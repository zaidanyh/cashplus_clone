package com.pasukanlangit.id.ui_cashgold_address.addupdate

sealed class AddUpdateAddressEvent {
    object GetProvinces: AddUpdateAddressEvent()
    data class GetCity(val provinces: String): AddUpdateAddressEvent()
    data class GetDistrict(val city: String): AddUpdateAddressEvent()
    data class GetVillage(val city: String, val district: String): AddUpdateAddressEvent()
    data class UpdateAddress(val id: Int?, val zipCode: String, val address: String, val province: String, val city: String, val district: String, val village: String, val isMain: Boolean?): AddUpdateAddressEvent()
    data class AddAddress(val zipCode: String, val address: String, val province: String, val city: String, val district: String, val village: String): AddUpdateAddressEvent()
    object RemoveHeadQueue: AddUpdateAddressEvent()
}
