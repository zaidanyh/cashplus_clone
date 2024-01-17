package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class HotelCityListRequestDto(

    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("hotel_city_name")
    val hotelCityName: String
)
