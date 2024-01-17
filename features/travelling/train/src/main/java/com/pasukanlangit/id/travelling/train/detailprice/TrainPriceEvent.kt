package com.pasukanlangit.id.travelling.train.detailprice

import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainBooking

sealed class TrainPriceEvent {
    object RemoveHeadMessage: TrainPriceEvent()
    data class TrainPrice(
        val from: String,
        val to: String,
        val date: String,
        val passenger: Passengers?,
        val trainBooking: TrainBooking?
    ): TrainPriceEvent()

    data class TrainBookingTicket(
        val idNumbers: String,
        val phone: String,
        val email: String,
        val from: String,
        val to: String,
        val date: String,
        val passengersName: String,
        val passenger: Passengers,
        val trainBooking: TrainBooking
    ): TrainPriceEvent()
    object RemoveHeadBookingMessage: TrainPriceEvent()

    data class BookingTransaction(
        val codeProduct: String,
        val destination: String?,
        val pin: String
    ): TrainPriceEvent()
    object RemoveHeadTransactionMessage: TrainPriceEvent()
}
