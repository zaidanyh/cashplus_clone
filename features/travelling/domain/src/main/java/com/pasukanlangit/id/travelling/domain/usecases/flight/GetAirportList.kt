package com.pasukanlangit.id.travelling.domain.usecases.flight

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.flight.AirportRequest
import com.pasukanlangit.id.travelling.domain.model.flight.AirportsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAirportList(
    private val repository: TravellingRepository
) {
    operator fun invoke(city: String): Flow<DataState<AirportsResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                throw Exception(Constants.invalidAuth)
            }

            val request = AirportRequest("", city, "", uuid)
            val response = repository.airportList(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}