package com.pasukanlangit.id.travelling.hotel.find

sealed class DetailHotelEvents {
    object RemoveHeadMessage: DetailHotelEvents()
    data class FindDetailHotel(
        val cityCode: String, val hotelCode: String,
        val checkIn: String, val checkOut: String, val room: String
    ): DetailHotelEvents()
}
