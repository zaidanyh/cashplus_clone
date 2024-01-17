package com.pasukanlangit.id.ui_cashgold_address.home

import com.pasukanlangit.id.model.Address

sealed class AddressAdapterEvent {
    data class UpdateToMainAddress(val address: Address): AddressAdapterEvent()
    data class UpdateAddressFull(val address: Address): AddressAdapterEvent()
    data class DeleteAddress(val id: Int): AddressAdapterEvent()
}
