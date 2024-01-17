package com.pasukanlangit.id.travelling.domain.model.hotel

data class HotelCityListResponse(
    val rc: String,
    val message: String,
    val data: List<HotelCityList>?
)

data class HotelCityList(
    val hotelCityName: String,
    val hotelCityCode: String
)