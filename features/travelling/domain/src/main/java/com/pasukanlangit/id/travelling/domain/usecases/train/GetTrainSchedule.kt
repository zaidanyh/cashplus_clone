package com.pasukanlangit.id.travelling.domain.usecases.train

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.train.TrainScheduleRequest
import com.pasukanlangit.id.travelling.domain.model.train.TrainScheduleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrainSchedule(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        from: String, to: String, date: String, adult: String, infant: String
    ): Flow<DataState<TrainScheduleResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TrainScheduleRequest(uuid, from, to, date, adult, infant)
            val response = repository.trainSchedule(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}