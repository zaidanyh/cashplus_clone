package com.pasukanlangit.id.travelling.train.init

import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainRoute

sealed class TrainEvents {
    object RemoveHeadMessage: TrainEvents()
    object StationsList: TrainEvents()
    data class SetDeparture(val departure: TrainRoute): TrainEvents()
    data class SetDestination(val destination: TrainRoute): TrainEvents()
    data class SetPassengers(val passengers: Passengers): TrainEvents()
}