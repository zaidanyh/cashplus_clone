package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineSummaryRequest
import com.pasukanlangit.id.domain_downline.model.DownlineTrxCount
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetDownLineTrxCount(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        dateStart: String,
        dateEnd: String
    ): Flow<DataState<DownlineTrxCount>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = DownLineSummaryRequest(
                uuid = uuid, dateStart = dateStart, dateEnd = dateEnd
            )
            val response = repository.successTrxCount(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}