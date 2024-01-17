package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter
import com.pasukanlangit.id.travelling.datasource.utils.JsonObjectConverter

data class FlightPriceResponseDto(
    @field:SerializedName("rc")
    var rc: String,

    @field:SerializedName("message")
    var message: String?,

    @field:SerializedName("msg")
    val msg: String?,

//    @JsonAdapter(value = JsonObjectConverter::class)
    @field:SerializedName("data")
    val data: ItemPriceData?,

    @field:SerializedName("endOfData")
    val endOfData: String?
)

data class ItemPriceData(

    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("flight")
    val flight: String,

    @field:SerializedName("flight_id")
    val flightId: String?,

    @field:SerializedName("flight_image")
    val flightImage: String?,

    @field:SerializedName("flight_code")
    val flightCode: String,

    @field:SerializedName("flight_from")
    val flightFrom: String,

    @field:SerializedName("flight_to")
    val flightTo: String,

    @field:SerializedName("flight_route")
    val flightRoute: String?,

    @field:SerializedName("flight_date")
    val flightDate: String,

    @field:SerializedName("flight_departure")
    val flightDeparture: String?,

    @field:SerializedName("flight_availableseat")
    val flightAvailableSeat: String,

    @field:SerializedName("flight_transit")
    val flightTransit: String,

    @field:SerializedName("flight_infotransit")
    val flightInfoTransit: String,

    @field:SerializedName("flight_time")
    val flightTime: String,

    @field:SerializedName("flight_class")
    val flightClass: String?,

    @field:SerializedName("flight_duration")
    val flightDuration: String?,

    @field:SerializedName("adult")
    val adult: Int,

    @field:SerializedName("child")
    val child: Int,

    @field:SerializedName("infant")
    val infant: Int,

    @field:SerializedName("publish")
    val publish: Int,

    @field:SerializedName("tax")
    val tax: Int,

    @field:SerializedName("totalfare")
    val totalFare: Int,

    @field:SerializedName("flight_shownta")
    val flightShownta: String? = null,

//    @JsonAdapter(value = JsonArrayConverter::class)
	@field:SerializedName("flight_baggage")
	val flightBaggage: List<ItemBaggage>?,

    @field:SerializedName("flight_facilities")
    val flightFacilities: String?,

    @field:SerializedName("flight_realnta")
    val flightRealNTA: String?,
)

data class ItemBaggage(
    @field:SerializedName("baggage")
    val baggage: String,

    @field:SerializedName("price")
    val price: Int
)

