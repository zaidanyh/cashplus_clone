package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter

data class FlightScheduleResponseDto(

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @JsonAdapter(value = JsonArrayConverter::class)
    @field:SerializedName("data")
    val data: List<AirportScheduleDataItem>?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class AirportScheduleDataItem(

    @field:SerializedName("flight_id")
    val flightId: String,

    @field:SerializedName("flight")
    val flight: String,

    @field:SerializedName("flight_code")
    val flightCode: String,

    @field:SerializedName("flight_image")
    val flightImage: String,

    @field:SerializedName("flight_from")
    val flightFrom: String,

    @field:SerializedName("flight_to")
    val flightTo: String,

    @field:SerializedName("flight_route")
    val flightRoute: String,

    @field:SerializedName("flight_date")
    val flightDate: String,

    @field:SerializedName("flight_transit")
    val flightTransit: String,

    @field:SerializedName("flight_infotransit")
    val flightInfotransit: String,

    @field:SerializedName("flight_datetime")
    val flightDatetime: String,

    @field:SerializedName("flight_duration")
    val flightDuration: String,

    @field:SerializedName("flight_price")
    val flightPrice: Int,

    @field:SerializedName("flight_publishedfare")
    val flightPublishFare: Int,

    @field:SerializedName("flight_seatavail")
    val flightSeatavail: Int,

    @field:SerializedName("flight_baggage")
    val flightBaggage: String,

    @field:SerializedName("flight_facilities")
    val flightFacilities: String
)
