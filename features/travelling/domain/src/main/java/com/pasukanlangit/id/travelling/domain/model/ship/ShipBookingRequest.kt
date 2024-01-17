package com.pasukanlangit.id.travelling.domain.model.ship

data class ShipBookingRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String,
    val shipName: String,
    val shipNumber: String,
    val shipCode: String,
    val shipClass: String,
    val adult: String,
    val infant: String,
    val passengername: String,
    val dateofbirth: String,
    val idnumber: String,
    val phone: String,
    val email: String
)
