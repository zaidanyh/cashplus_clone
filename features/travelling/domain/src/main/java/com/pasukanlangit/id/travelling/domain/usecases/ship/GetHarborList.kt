package com.pasukanlangit.id.travelling.domain.usecases.ship

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.ship.HarborCitiesRequest
import com.pasukanlangit.id.travelling.domain.model.ship.HarborCitiesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetHarborList(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        name: String
    ): Flow<DataState<HarborCitiesResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = HarborCitiesRequest(uuid, "", name)
            val response = repository.harborsList(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}