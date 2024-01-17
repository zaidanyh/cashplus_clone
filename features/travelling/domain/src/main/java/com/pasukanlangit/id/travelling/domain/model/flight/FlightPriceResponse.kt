package com.pasukanlangit.id.travelling.domain.model.flight

data class FlightPriceResponse(
    val flight: String,
    val flightCode: String,
    val flightFrom: String,
    val flightTo: String,
    val flightRoute: String?,
    val flightDate: String,
    val flightDeparture: String?,
    val flightSeatAvailable: String,
    val flightTransit: String,
    val flightInfoTransit: String,
    val flightTime: String,
    val flightClass: String?,
    val adult: Int,
    val child: Int,
    val infant: Int,
    val publish: Int,
    val tax: Int,
    val totalFare: Int,
//    val baggage: List<FlightPriceBaggage>?,
//    val flightFacilities: String?,
//    val flightRealNTA: Int?,
)

data class FlightPriceBaggage(
    val baggage: String,
    val price: Int
)
