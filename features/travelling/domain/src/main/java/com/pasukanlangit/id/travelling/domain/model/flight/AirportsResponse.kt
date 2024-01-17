package com.pasukanlangit.id.travelling.domain.model.flight

data class AirportsResponse(
    val rc: String,
    val message: String,
    val data: List<Airports>?
)

data class Airports(
    val code: String,
    val city: String,
    val group: String,
    val airport: String,
    val status: String
)
