package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FlightBookingRequestDto(

    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("flight_code")
    val flightCode: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("child")
    val child: String,

    @field:SerializedName("infant")
    val infant: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("passengername")
    val passengerName: String,

    @field:SerializedName("dateofbirth")
    val dateofBirth: String,

    @field:SerializedName("baggagevolume")
    val baggageVolume: String,

    @field:SerializedName("passportnumber")
    val passportNumber: String,

    @field:SerializedName("passportexpired")
    val passportExpired: String
)
