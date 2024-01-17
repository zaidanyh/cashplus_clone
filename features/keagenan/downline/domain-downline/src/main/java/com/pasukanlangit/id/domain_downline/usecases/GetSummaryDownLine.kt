package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineSumDetRequest
import com.pasukanlangit.id.domain_downline.model.DownLineSummaryRequest
import com.pasukanlangit.id.domain_downline.model.SummaryDownlineResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSummaryDownLine(
    private val repository: DownLineRepository
) {
    operator fun invoke(dateStart: String, dateEnd: String): Flow<DataState<SummaryDownlineResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = DownLineSummaryRequest(
                uuid = uuid, dateStart = dateStart, dateEnd = dateEnd
            )
            val response = repository.getSummaryDownline(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}