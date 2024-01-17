package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.RateRequest
import com.pasukanlangit.id.cash_transfer.domain.model.RateResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRateConversion(
    private val repository: TransferRepository
) {
    operator fun invoke(currency: String, amount: String): Flow<DataState<RateResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = RateRequest(uuid = uuid, currency = currency, amount = amount)
            val response = repository.rateConversion(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}