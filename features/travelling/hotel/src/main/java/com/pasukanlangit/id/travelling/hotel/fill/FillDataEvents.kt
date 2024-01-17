package com.pasukanlangit.id.travelling.hotel.fill

sealed class FillDataEvents {
    object CheckBalance: FillDataEvents()
    object RemoveHeadMessage: FillDataEvents()

    data class HotelBooking(
        val hotelCode: String, val hotelKey: String, val rateKey: String, val checkIn: String,
        val checkOut: String, val hotelRoom: String, val paxName: String, val email: String,
        val phone: String, val hotelRequest: String
    ): FillDataEvents()
    object RemoveHeadMessageBooking: FillDataEvents()

    data class HotelBookingTransaction(
        val codeProduct: String,
        val destination: String?,
        val pin: String
    ): FillDataEvents()
    object RemoveHeadMessageTransaction: FillDataEvents()
}