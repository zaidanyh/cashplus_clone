package com.pasukanlangit.id.travelling.domain.usecases.ship

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.ship.ShipBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetShipBooking(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        from: String, to: String, date: String, shipName: String, shipNumber: String,
        shipCode: String, shipClass: String, adult: String, infant: String, passengerName: String,
        dateOfBirth: String, idNumber: String, phone: String, email: String
    ): Flow<DataState<BookingCodeResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = ShipBookingRequest(
                uuid, from, to, date, shipName, shipNumber, shipCode, shipClass,
                adult, infant, passengerName, dateOfBirth, idNumber, phone, email
            )
            val response = repository.shipBooking(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}