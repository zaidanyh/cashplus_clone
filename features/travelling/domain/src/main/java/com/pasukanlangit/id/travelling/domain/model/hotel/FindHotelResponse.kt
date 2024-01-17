package com.pasukanlangit.id.travelling.domain.model.hotel

data class FindHotelResponse(
    val rc: String,
    val message: String,
    val data: List<Hotels>?
)

data class Hotels(
    val key: String,
    val code: String,
    val name: String,
    val rating: String?,
    val address: String?,
    val city: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val marketName: String,
    val checkin: String,
    val checkout: String,
    val images: List<String>?,
    val remark: String?,
    val extraBeds: String?,
    val facilities: List<String>?,
    val room: List<RoomHotel>?
)

data class RoomHotel(
    val rateKey: String,
    val code: String?,
    val name: String,
    val request: String,
    val price: Int,
    val nta: Int,
    val image: String?,
    val boardName: String,
    val cancellation: String?,
    val included: String?
)
