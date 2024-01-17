package com.pasukanlangit.id.travelling.domain.model.ship

data class HarborScheduleResponse(
    val rc: String,
    val message: String,
    val data: List<HarborSchedules>?
)

data class HarborSchedules(
    val name: String,
    val number: String,
    val code: String,
    val from: String,
    val to: String,
    val route: String,
    val date: String,
    val dateTime: String,
    val infoDateTime: String,
    val infoRoute: String,
    val shipClass: String,
    val basicFare: Int,
    val admin: Int,
    val price: Int,
    val maleSeat: String,
    val femaleSeat: String
)
