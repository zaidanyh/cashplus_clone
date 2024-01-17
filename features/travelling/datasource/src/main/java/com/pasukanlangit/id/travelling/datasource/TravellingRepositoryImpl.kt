package com.pasukanlangit.id.travelling.datasource

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.travelling.datasource.mapper.*
import com.pasukanlangit.id.travelling.datasource.network.TravellingService
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.*
import com.pasukanlangit.id.travelling.domain.model.flight.*
import com.pasukanlangit.id.travelling.domain.model.hotel.*
import com.pasukanlangit.id.travelling.domain.model.ship.*
import com.pasukanlangit.id.travelling.domain.model.train.*

@Suppress("BlockingMethodInNonBlockingContext")
class TravellingRepositoryImpl(
    private val travellingApi: TravellingService,
    private val sharedPref: SharedPrefDataSource,
    private val errorParser: GlobalErrorParser
): TravellingRepository {

    override suspend fun balanceCheck(
        request: BalanceRequest,
        accessToken: String
    ): BalanceResponse {
        val response = travellingApi.balanceCheck(request.toBalanceRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toBalanceResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun bookingTransaction(
        request: TransactionRequest,
        accessToken: String
    ): TransactionResponse {
        val response = travellingApi.bookingTransaction(request.toTransactionRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toTransactionResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun airportList(
        request: AirportRequest,
        accessToken: String
    ): AirportsResponse {
        val response = travellingApi.airportsList(request.toAirportRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toListAirportResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun flightSchedule(
        request: FlightScheduleRequest,
        accessToken: String
    ): FlightScheduleResponse {
        val response = travellingApi.flightSchedule(request.toFlightScheduleRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toListFlightScheduleResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun flightPrice(
        request: FlightPriceRequest,
        accessToken: String
    ): FlightPriceResponse {
        val response = travellingApi.flightPrice(request.toFlightPriceRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toFlightPriceResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun flightBooking(
        request: FlightBookingRequest,
        accessToken: String
    ): BookingCodeResponse {
        val response = travellingApi.flightBooking(request.toFlightBookingRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBookingCodeResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun cityListHotel(
        request: HotelCityListRequest,
        accessToken: String
    ): HotelCityListResponse {
        val response = travellingApi.cityList(request.toHotelCityListRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toHotelCityListResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun findHotel(
        request: FindHotelRequest,
        accessToken: String
    ): FindHotelResponse {
        val response = travellingApi.findHotel(request.toFindHotelRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFindHotelResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun findHotelByCity(
        request: FindHotelByCityRequest,
        accessToken: String
    ): FindHotelResponse {
        val response = travellingApi.findHotelByCity(request.toFindHotelRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFindHotelResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun hotelBooking(
        request: HotelBookingRequest,
        accessToken: String
    ): BookingCodeResponse {
        val response = travellingApi.hotelBooking(request.toBookingHotelRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toPaymentCodeResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun trainStations(
        request: StationsRequest,
        accessToken: String
    ): StationsResponse {
        val response = travellingApi.stationList(request.toStationsRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toStationsResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun trainSchedule(
        request: TrainScheduleRequest,
        accessToken: String
    ): TrainScheduleResponse {
        val response = travellingApi.trainSchedule(request.toTrainScheduleRequestDto(), accessToken)
        if (response.isSuccessful) {
            if (response.body()?.rc == "00") {
                response.body()?.let { data -> return data.toTrainScheduleResponse() }
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun trainPrice(
        request: TrainPriceRequest,
        accessToken: String
    ): TrainPriceResponse {
        val response = travellingApi.trainPrice(request.toTrainPriceRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTrainPriceResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun trainBooking(
        request: TrainBookingRequest,
        accessToken: String
    ): BookingCodeResponse {
        val response = travellingApi.trainBooking(request.toTrainBookingRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBookingCodeResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun harborsList(
        request: HarborCitiesRequest,
        accessToken: String
    ): HarborCitiesResponse {
        val response = travellingApi.harborList(request.toHarborRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toHarborCitiesResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun harborSchedule(
        request: HarborScheduleRequest,
        accessToken: String
    ): HarborScheduleResponse {
        val response = travellingApi.shipSchedule(request.toHarborScheduleRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toHarborScheduleResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun shipBooking(
        request: ShipBookingRequest,
        accessToken: String
    ): BookingCodeResponse {
        val response = travellingApi.shipBooking(request.toShipBookingRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toPaymentCodeResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()
    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}