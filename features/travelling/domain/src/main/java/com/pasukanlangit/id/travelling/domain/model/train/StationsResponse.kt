package com.pasukanlangit.id.travelling.domain.model.train

data class StationsResponse(
    val rc: String,
    val message: String,
    val data: List<Stations>?
)

data class Stations(
    val code: String,
    val location: String
)
