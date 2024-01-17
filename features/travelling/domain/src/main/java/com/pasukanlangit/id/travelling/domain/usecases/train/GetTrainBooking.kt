package com.pasukanlangit.id.travelling.domain.usecases.train

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.train.TrainBookingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrainBooking(
    private val repository: TravellingRepository
) {
    operator fun invoke(
         session: String, from: String, to: String, date: String, adult: String, infant: String,
         trainCode: String, trainClass: String, trainSubClass: String, passengerName: String,
         idNumber: String, phone: String, email: String
    ): Flow<DataState<BookingCodeResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TrainBookingRequest(uuid, session, from, to, date, adult, infant, trainCode, trainClass, trainSubClass, passengerName, idNumber, phone, email)
            val response = repository.trainBooking(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}