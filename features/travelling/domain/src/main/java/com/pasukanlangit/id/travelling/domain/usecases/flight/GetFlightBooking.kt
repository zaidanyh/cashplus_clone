package com.pasukanlangit.id.travelling.domain.usecases.flight

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.flight.FlightBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFlightBooking(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        from: String, to: String, date:String, flightCode: String, adult: String, child: String,
        infant: String, email: String, phone: String, passengersName: String,
        passengersDateOfBirth: String, baggageVolumes: String
    ): Flow<DataState<BookingCodeResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = FlightBookingRequest(
                uuid, from, to, date, flightCode, adult, child, infant, email, phone,
                passengersName, passengersDateOfBirth, baggageVolumes, "", ""
            )
            val response = repository.flightBooking(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}