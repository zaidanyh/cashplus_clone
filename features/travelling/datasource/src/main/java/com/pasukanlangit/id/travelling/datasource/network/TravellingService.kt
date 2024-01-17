package com.pasukanlangit.id.travelling.datasource.network

import com.pasukanlangit.id.core.model.BalanceRequestCore
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.travelling.datasource.network.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TravellingService {
    @POST("balance/check")
    suspend fun balanceCheck(@Body request: BalanceRequestCore, @Header("x_access_token") accessToken: String): Response<BalanceResponseCore>
    @POST("trx/send")
    suspend fun bookingTransaction(@Body request: TransactionRequestDto, @Header("x_access_token") accessToken: String): Response<TransactionResponseDto>

    // FLIGHT
    @POST("mmbc/flight/airport")
    suspend fun airportsList(@Body request: AirportRequestDto, @Header("x_access_token") accessToken: String): Response<AirportsResponseDto>
    @POST("mmbc/flight/schedule")
    suspend fun flightSchedule(@Body request: FlightScheduleRequestDto, @Header("x_access_token") accessToken: String): Response<FlightScheduleResponseDto>
    @POST("mmbc/flight/price")
    suspend fun flightPrice(@Body request: FlightPriceRequestDto, @Header("x_access_token") accessToken: String): Response<FlightPriceResponseDto>
    @POST("mmbc/flight/book")
    suspend fun flightBooking(@Body request: FlightBookingRequestDto, @Header("x_access_token") accessToken: String): Response<BookingResponseDto>

    // TRAIN
    @POST("mmbc/train/stations")
    suspend fun stationList(@Body request: StationsRequestDto, @Header("x_access_token") accessToken: String): Response<StationsResponseDto>
    @POST("mmbc/train/schedule")
    suspend fun trainSchedule(@Body request: TrainScheduleRequestDto, @Header("x_access_token") accessToken: String): Response<TrainScheduleResponseDto>
    @POST("mmbc/train/price")
    suspend fun trainPrice(@Body request: TrainPriceRequestDto, @Header("x_access_token") accessToken: String): Response<TrainPriceResponseDto>
    @POST("mmbc/train/book")
    suspend fun trainBooking(@Body request: TrainBookingRequestDto, @Header("x_access_token") accessToken: String): Response<BookingResponseDto>

    // SHIP
    @POST("mmbc/ship/harbor")
    suspend fun harborList(@Body request: HarborsRequestDto, @Header("x_access_token") accessToken: String): Response<HarborsResponseDto>
    @POST("mmbc/ship/schedule")
    suspend fun shipSchedule(@Body request: ShipScheduleRequestDto, @Header("x_access_token") accessToken: String): Response<ShipScheduleResponseDto>
    @POST("mmbc/ship/book")
    suspend fun shipBooking(@Body request: ShipBookRequestDto, @Header("x_access_token") accessToken: String): Response<BookingResponseDto>

    // HOTEL
    @POST("mmbc/hotel/city_list")
    suspend fun cityList(@Body request: HotelCityListRequestDto, @Header("x_access_token") accessToken: String): Response<HotelCityListResponseDto>
    @POST("mmbc/hotel/search")
    suspend fun findHotel(@Body request: FindHotelRequestDto, @Header("x_access_token") accessToken: String): Response<FindHotelResponseDto>
    @POST("mmbc/hotel/search_by_city")
    suspend fun findHotelByCity(@Body request: FindHotelRequestDto, @Header("x_access_token") accessToken: String): Response<FindHotelResponseDto>
    @POST("mmbc/hotel/book")
    suspend fun hotelBooking(@Body request: BookingHotelRequestDto, @Header("x_access_token") accessToken: String): Response<BookingResponseDto>
}