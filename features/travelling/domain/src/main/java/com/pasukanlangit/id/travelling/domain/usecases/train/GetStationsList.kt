package com.pasukanlangit.id.travelling.domain.usecases.train

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.train.StationsRequest
import com.pasukanlangit.id.travelling.domain.model.train.StationsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStationsList(
    private val repository: TravellingRepository
) {
    operator fun invoke(location: String): Flow<DataState<StationsResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = StationsRequest(uuid, "", location)
            val response = repository.trainStations(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}