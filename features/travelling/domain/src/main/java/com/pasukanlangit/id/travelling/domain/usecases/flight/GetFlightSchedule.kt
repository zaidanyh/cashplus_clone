package com.pasukanlangit.id.travelling.domain.usecases.flight

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.flight.FlightScheduleRequest
import com.pasukanlangit.id.travelling.domain.model.flight.FlightScheduleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFlightSchedule(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        departure: String, destination: String, date: String
    ): Flow<DataState<FlightScheduleResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                throw Exception(Constants.invalidAuth)
            }

            val request = FlightScheduleRequest(uuid, departure, destination, date)
            val response = repository.flightSchedule(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}