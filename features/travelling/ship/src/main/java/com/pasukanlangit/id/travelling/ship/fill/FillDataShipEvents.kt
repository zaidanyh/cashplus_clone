package com.pasukanlangit.id.travelling.ship.fill

import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipSchedules

sealed class FillDataShipEvents {
    object RemoveHeadMessage: FillDataShipEvents()
    object CheckBalance: FillDataShipEvents()

    data class ShipBooking(
        val from: String, val to: String, val date: String, val shipSchedules: ShipSchedules,
        val passengers: ShipPassengers, val passengerName: String, val dateOfBirth: String,
        val idNumber: String, val phone: String, val email: String
    ): FillDataShipEvents()
    object RemoveHeadBookingMessage: FillDataShipEvents()

    data class ShipBookingTransaction(
        val codeProduct: String,
        val destination: String?,
        val pin: String
    ): FillDataShipEvents()
    object RemoveHeadMessageTransaction: FillDataShipEvents()
}