package com.pasukanlangit.id.travelling.domain.usecases

import com.pasukanlangit.id.travelling.domain.usecases.flight.GetAirportList
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightBooking
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightPrice
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightSchedule
import com.pasukanlangit.id.travelling.domain.usecases.hotel.FindHotel
import com.pasukanlangit.id.travelling.domain.usecases.hotel.FindHotelByCity
import com.pasukanlangit.id.travelling.domain.usecases.hotel.GetCityList
import com.pasukanlangit.id.travelling.domain.usecases.hotel.GetHotelBooking
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetHarborList
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetHarborSchedule
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetShipBooking
import com.pasukanlangit.id.travelling.domain.usecases.train.GetStationsList
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainBooking
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainPrice
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainSchedule

data class TravellingUseCases(
    val checkBalance: BalanceUseCase,
    val booking: BookingUseCase,
    val airportList: GetAirportList,
    val flightSchedule: GetFlightSchedule,
    val flightPrice: GetFlightPrice,
    val flightBooking: GetFlightBooking,
    val hotelCityList: GetCityList,
    val findHotel: FindHotel,
    val findHotelByCity: FindHotelByCity,
    val hotelBooking: GetHotelBooking,
    val stations: GetStationsList,
    val trainSchedule: GetTrainSchedule,
    val trainPrice: GetTrainPrice,
    val trainBooking: GetTrainBooking,
    val harbors: GetHarborList,
    val harborSchedule: GetHarborSchedule,
    val shipBooking: GetShipBooking
)