package com.pasukanlangit.id.travelling.domain.model.ship

data class HarborScheduleRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String,
    val adult: String,
    val infant: String,
    val male: String,
    val female: String
)
