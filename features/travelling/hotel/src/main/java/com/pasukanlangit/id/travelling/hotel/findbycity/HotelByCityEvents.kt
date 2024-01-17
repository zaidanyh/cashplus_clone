package com.pasukanlangit.id.travelling.hotel.findbycity

sealed class HotelByCityEvents {
    object RemoveHeadMessage: HotelByCityEvents()
    data class FindHotelByCity(
        val cityCode: String, val checkIn: String,
        val checkOut: String, val roomTotal: String
    ): HotelByCityEvents()
}