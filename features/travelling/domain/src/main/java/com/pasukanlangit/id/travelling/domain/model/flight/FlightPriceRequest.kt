package com.pasukanlangit.id.travelling.domain.model.flight

data class FlightPriceRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String,
    val flightCode: String,
    val adult: String,
    val child: String,
    val infant: String
)
