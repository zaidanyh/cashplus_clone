package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter

data class HotelCityListResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @JsonAdapter(value = JsonArrayConverter::class)
    @field:SerializedName("data")
    val data: List<ItemHotelCityList>?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class ItemHotelCityList(
    @field:SerializedName("hotel_city_name")
    val hotelCityName: String,

    @field:SerializedName("hotel_city_code")
    val hotelCityCode: String
)