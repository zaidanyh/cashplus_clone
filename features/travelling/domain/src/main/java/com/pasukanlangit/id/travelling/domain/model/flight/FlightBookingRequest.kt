package com.pasukanlangit.id.travelling.domain.model.flight

data class FlightBookingRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String,
    val flightCode: String,
    val adult: String,
    val child: String,
    val infant: String,
    val email: String,
    val phone: String,
    val passengersName: String,
    val passengersDateOfBirth: String,
    val baggageVolumes: String,
    val passportsNumbers: String,
    val passportsExpired: String
)
