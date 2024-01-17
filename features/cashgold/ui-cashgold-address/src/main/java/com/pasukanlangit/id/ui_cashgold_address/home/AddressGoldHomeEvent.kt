package com.pasukanlangit.id.ui_cashgold_address.home

sealed class AddressGoldHomeEvent {
    object GetAddressList: AddressGoldHomeEvent()
    object RemoveHeadQueue: AddressGoldHomeEvent()
    data class DeleteAddress(val id: Int): AddressGoldHomeEvent()
    data class UpdateAddress(val id: Int, val zipCode: String, val address: String, val province: String, val city: String, val district: String, val village: String, val isMain: Boolean): AddressGoldHomeEvent()
}
