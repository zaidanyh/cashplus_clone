package com.pasukanlangit.id.travelling.domain.model.flight

data class FlightScheduleResponse(
    val rc: String,
    val message: String,
    val data: List<FlightSchedule>?
)

data class FlightSchedule(
    val flightId: String,
    val flight: String,
    val flightCode: String,
    val flightImage: String,
    val flightFrom: String,
    val flightTo: String,
    val flightRoute: String,
    val flightDate: String,
    val flightTransit: String,
    val flightInfoTransit: String,
    val flightTime: String,
    val flightDuration: String,
    val flightPrice: Int,
    val flightPublishFare: Int,
    val flightSeatAvailable: Int,
    val flightBaggage: String,
    val flightFacilities: String
)
