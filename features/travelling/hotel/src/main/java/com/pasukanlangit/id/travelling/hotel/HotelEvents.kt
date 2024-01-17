package com.pasukanlangit.id.travelling.hotel

import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor

sealed class HotelEvents {
    object RemoveHeadMessage: HotelEvents()
    object CityList: HotelEvents()
    data class SetCitySelected(val citySelected: CitySelected): HotelEvents()
    data class SetRoomVisitor(val roomVisitor: RoomVisitor): HotelEvents()
}