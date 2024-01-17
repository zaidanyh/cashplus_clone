package com.pasukanlangit.id.travelling.domain.model.hotel

data class FindHotelByCityRequest(
    val uuid: String,
    val hotelCityCode: String,
    val hotelCheckin: String,
    val hotelCheckout: String,
    val hotelRoom: String
)
