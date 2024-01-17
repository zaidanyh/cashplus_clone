package com.pasukanlangit.id.recapitulation.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.model.RecapRequest
import com.pasukanlangit.id.recapitulation.domain.utils.RecapTransResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllRecapTrans(
    private val repository: RecapRepository
) {
    operator fun invoke(dateStart: String, dateEnd: String): Flow<DataState<RecapTransResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = RecapRequest(uuid = uuid, dateStart = dateStart, dateEnd = dateEnd)
            val summary = repository.getProfitSummary(request, accessToken)
            val byProduct = repository.getProfitByProduct(request, accessToken)
            val response = RecapTransResponse(
                capitalTotal = summary.modal, sellingTotal = summary.selling, profit = summary.profit,
                products = byProduct
            )
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.networkError))
        }
    }
}