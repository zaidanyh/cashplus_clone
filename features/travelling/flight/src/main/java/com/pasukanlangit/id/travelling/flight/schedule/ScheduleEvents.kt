package com.pasukanlangit.id.travelling.flight.schedule

sealed class ScheduleEvents {
    object RemoveHeadMessage: ScheduleEvents()
    data class FlightSchedule(
        val departure: String,
        val destination: String,
        val date: String
    ): ScheduleEvents()
}