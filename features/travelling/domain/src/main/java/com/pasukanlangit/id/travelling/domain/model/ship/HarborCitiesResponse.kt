package com.pasukanlangit.id.travelling.domain.model.ship

data class HarborCitiesResponse(
    val rc: String,
    val message: String,
    val data: List<Harbors>?
)

data class Harbors(
    val code: String,
    val name: String
)