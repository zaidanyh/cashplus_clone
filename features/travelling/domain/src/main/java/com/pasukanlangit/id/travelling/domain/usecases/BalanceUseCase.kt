package com.pasukanlangit.id.travelling.domain.usecases

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BalanceRequest
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BalanceUseCase(
    private val repository: TravellingRepository
) {
    operator fun invoke(): Flow<DataState<BalanceResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = BalanceRequest(uuid)
            val response = repository.balanceCheck(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}