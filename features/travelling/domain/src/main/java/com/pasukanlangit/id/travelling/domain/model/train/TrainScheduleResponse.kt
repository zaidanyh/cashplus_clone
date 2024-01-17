package com.pasukanlangit.id.travelling.domain.model.train

data class TrainScheduleResponse(
    val rc: String,
    val message: String,
    val session: String,
    val data: List<TrainItemSchedule>?
)
data class TrainItemSchedule(
    val name: String,
    val code: String,
    val id: String,
    val from: String,
    val to: String,
    val route: String,
    val date: String,
    val timeDeparture: String,
    val timeArrival: String,
    val trainDateTime: String,
    val price: Int,
    val trainClass: String,
    val subClass: String,
    val available: String
)
