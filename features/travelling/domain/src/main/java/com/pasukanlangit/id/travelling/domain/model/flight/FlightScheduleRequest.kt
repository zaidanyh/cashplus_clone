package com.pasukanlangit.id.travelling.domain.model.flight

data class FlightScheduleRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String
)
