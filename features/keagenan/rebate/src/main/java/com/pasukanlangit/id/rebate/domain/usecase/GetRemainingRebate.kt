package com.pasukanlangit.id.rebate.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.rebate.domain.RebateRepository
import com.pasukanlangit.id.rebate.domain.model.RemainingRebateRequest
import com.pasukanlangit.id.rebate.domain.model.RemainingRebateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRemainingRebate(
    private val repository: RebateRepository
) {
    operator fun invoke(): Flow<DataState<RemainingRebateResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = RemainingRebateRequest(uuid)
            val response = repository.checkRemainingRebate(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}