package com.pasukanlangit.id.travelling.domain.model.hotel

data class FindHotelRequest(
    val uuid: String,
    val hotelCityCode: String,
    val hotelCode: String,
    val hotelCheckin: String,
    val hotelCheckout: String,
    val hotelRoom: String
)
