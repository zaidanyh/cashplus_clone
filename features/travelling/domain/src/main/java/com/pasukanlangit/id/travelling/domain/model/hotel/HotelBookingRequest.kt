package com.pasukanlangit.id.travelling.domain.model.hotel

data class HotelBookingRequest(
    val uuid: String,
    val hotelCode: String,
    val hotelKey: String,
    val rateKey: String,
    val checkIn: String,
    val checkOut: String,
    val hotelRoom: String,
    val paxName: String,
    val email: String,
    val phone: String,
    val hotelRequest: String
)
