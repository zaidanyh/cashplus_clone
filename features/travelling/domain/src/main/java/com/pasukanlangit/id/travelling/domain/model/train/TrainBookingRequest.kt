package com.pasukanlangit.id.travelling.domain.model.train

data class TrainBookingRequest(
    val uuid: String,
    val session: String,
    val from: String,
    val to: String,
    val date: String,
    val adult: String,
    val infant: String,
    val trainCode: String,
    val trainClass: String,
    val trainSubClass: String,
    val passengerName: String,
    val idNumber: String,
    val phone: String,
    val email: String
)
