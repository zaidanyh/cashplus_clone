package com.pasukanlangit.id.travelling.domain

import com.pasukanlangit.id.travelling.domain.model.*
import com.pasukanlangit.id.travelling.domain.model.flight.*
import com.pasukanlangit.id.travelling.domain.model.hotel.*
import com.pasukanlangit.id.travelling.domain.model.ship.*
import com.pasukanlangit.id.travelling.domain.model.train.*

interface TravellingRepository {
    suspend fun balanceCheck(request: BalanceRequest, accessToken: String): BalanceResponse
    suspend fun bookingTransaction(request: TransactionRequest, accessToken: String): TransactionResponse

    suspend fun airportList(request: AirportRequest, accessToken: String): AirportsResponse
    suspend fun flightSchedule(request: FlightScheduleRequest, accessToken: String): FlightScheduleResponse
    suspend fun flightPrice(request: FlightPriceRequest, accessToken: String): FlightPriceResponse
    suspend fun flightBooking(request: FlightBookingRequest, accessToken: String): BookingCodeResponse

    suspend fun cityListHotel(request: HotelCityListRequest, accessToken: String): HotelCityListResponse
    suspend fun findHotel(request: FindHotelRequest, accessToken: String): FindHotelResponse
    suspend fun findHotelByCity(request: FindHotelByCityRequest, accessToken: String): FindHotelResponse
    suspend fun hotelBooking(request: HotelBookingRequest, accessToken: String): BookingCodeResponse

    suspend fun trainStations(request: StationsRequest, accessToken: String): StationsResponse
    suspend fun trainSchedule(request: TrainScheduleRequest, accessToken: String): TrainScheduleResponse
    suspend fun trainPrice(request: TrainPriceRequest, accessToken: String): TrainPriceResponse
    suspend fun trainBooking(request: TrainBookingRequest, accessToken: String): BookingCodeResponse

    suspend fun harborsList(request: HarborCitiesRequest, accessToken: String): HarborCitiesResponse
    suspend fun harborSchedule(request: HarborScheduleRequest, accessToken: String): HarborScheduleResponse
    suspend fun shipBooking(request: ShipBookingRequest, accessToken: String): BookingCodeResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}