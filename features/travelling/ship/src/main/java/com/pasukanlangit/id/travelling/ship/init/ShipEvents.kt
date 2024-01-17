package com.pasukanlangit.id.travelling.ship.init

import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute

sealed class ShipEvents {
    object RemoveHeadMessage: ShipEvents()
    object HarborsShip: ShipEvents()
    data class SetDeparture(val departure: ShipRoute): ShipEvents()
    data class SetDestination(val destination: ShipRoute): ShipEvents()
    data class SetPassengers(val passengers: ShipPassengers): ShipEvents()
}