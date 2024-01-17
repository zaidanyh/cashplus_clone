package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FlightPriceRequestDto(
    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("flight_code")
    val flightCode: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String,

    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("child")
    val child: String
)
