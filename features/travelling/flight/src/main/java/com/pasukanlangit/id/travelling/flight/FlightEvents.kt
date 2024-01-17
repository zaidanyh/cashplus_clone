package com.pasukanlangit.id.travelling.flight

import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import com.pasukanlangit.id.travelling.flight.temp.Passengers

sealed class FlightEvents {
    object RemoveHeadMessage: FlightEvents()
    object AirportList: FlightEvents()
    data class SetPassengers(val passengers: Passengers): FlightEvents()
    data class SetDeparture(val departure: FlightRoute): FlightEvents()
    data class SetDestination(val destination: FlightRoute): FlightEvents()
}