package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FindHotelRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("hotel_city_code")
    val hotelCityCode: String,

    @field:SerializedName("hotel_code")
    val hotelCode: String? = null,

    @field:SerializedName("hotel_checkin")
    val hotelCheckin: String,

    @field:SerializedName("hotel_checkout")
    val hotelCheckout: String,

    @field:SerializedName("hotel_room")
    val hotelRoom: String
)
