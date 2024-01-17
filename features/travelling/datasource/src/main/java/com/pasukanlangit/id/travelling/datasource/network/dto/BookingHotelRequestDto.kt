package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class BookingHotelRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("hotel_code")
    val hotelCode: String,

    @field:SerializedName("hotel_key")
    val hotelKey: String,

    @field:SerializedName("room_rate_key")
    val roomRateKey: String,

    @field:SerializedName("hotel_checkin")
    val hotelCheckin: String,

    @field:SerializedName("hotel_checkout")
    val hotelCheckout: String,

    @field:SerializedName("hotel_room")
    val hotelRoom: String,

    @field:SerializedName("hotel_paxname")
    val hotelPaxname: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("hotel_request")
    val hotelRequest: String
)
