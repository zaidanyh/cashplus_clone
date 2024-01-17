package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineSumDetRequest
import com.pasukanlangit.id.domain_downline.model.SummaryTransferResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSummaryTransferDownLine(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        dateStart: String,
        dateEnd: String,
        downLinePhone: String,
    ): Flow<DataState<List<SummaryTransferResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val request = DownLineSumDetRequest(
                dateStart = dateStart,
                dateEnd = dateEnd,
                downlinePhone = downLinePhone,
                uuid = uuid
            )
            val response = repository.getSummaryTransfer(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}