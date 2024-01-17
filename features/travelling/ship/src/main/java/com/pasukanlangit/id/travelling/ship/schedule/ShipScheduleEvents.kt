package com.pasukanlangit.id.travelling.ship.schedule

import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers

sealed class ShipScheduleEvents {
    object RemoveHeadMessage: ShipScheduleEvents()
    data class ShipSchedule(
        val from: String,
        val to: String,
        val date: String,
        val passengers: ShipPassengers?
    ): ShipScheduleEvents()
}
