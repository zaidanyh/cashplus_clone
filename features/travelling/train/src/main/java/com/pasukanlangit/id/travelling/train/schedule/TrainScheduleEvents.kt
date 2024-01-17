package com.pasukanlangit.id.travelling.train.schedule

sealed class TrainScheduleEvents {
    object RemoveHeadMessage: TrainScheduleEvents()
    data class TrainSchedule(
        val from: String, val to: String,
        val date: String, val adult: String,
        val infant: String
    ): TrainScheduleEvents()
}