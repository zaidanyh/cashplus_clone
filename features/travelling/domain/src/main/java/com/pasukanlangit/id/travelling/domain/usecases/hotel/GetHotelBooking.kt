package com.pasukanlangit.id.travelling.domain.usecases.hotel

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.hotel.HotelBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetHotelBooking(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        hotelCode: String, hotelKey: String, rateKey: String, checkIn: String, checkOut: String,
        hotelRoom: String, paxName: String, email: String, phone: String, hotelRequest: String
    ): Flow<DataState<BookingCodeResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = HotelBookingRequest(uuid, hotelCode, hotelKey, rateKey, checkIn, checkOut, hotelRoom, paxName, email, phone, hotelRequest)
            val response = repository.hotelBooking(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}