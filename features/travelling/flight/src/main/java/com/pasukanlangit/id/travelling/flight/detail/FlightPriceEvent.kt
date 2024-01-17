package com.pasukanlangit.id.travelling.flight.detail

sealed class FlightPriceEvent {
    object RemoveHeadMessage: FlightPriceEvent()
    data class FlightPrice(
        val departureCode: String,
        val destinationCode: String,
        val date: String,
        val flightCode: String,
        val adult: String,
        val child: String,
        val infant: String
    ): FlightPriceEvent()

    data class FlightBooking(
        val from: String,
        val to: String,
        val date:String,
        val flightCode: String,
        val adult: String,
        val child: String,
        val infant: String,
        val email: String,
        val phone: String,
        val passengersName: String,
        val passengersDateOfBirth: String,
        val baggageVolumes: String
    ): FlightPriceEvent()
    object RemoveHeadMessageBooking: FlightPriceEvent()

    data class BookingTransaction(
        val codeProduct: String,
        val destination: String?,
        val pin: String
    ): FlightPriceEvent()
    object RemoveHeadMessageTransaction: FlightPriceEvent()
}