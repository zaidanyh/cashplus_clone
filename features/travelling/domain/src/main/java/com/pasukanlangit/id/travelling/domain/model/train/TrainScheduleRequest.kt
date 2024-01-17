package com.pasukanlangit.id.travelling.domain.model.train

data class TrainScheduleRequest(
    val uuid: String,
    val from: String,
    val to: String,
    val date: String,
    val adult: String,
    val infant: String
)