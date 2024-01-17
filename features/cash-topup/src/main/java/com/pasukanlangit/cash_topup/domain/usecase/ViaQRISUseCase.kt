package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.TopUpViaQRISRequest
import com.pasukanlangit.cash_topup.domain.model.TopUpViaQRISResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ViaQRISUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(amount: String): Flow<DataState<TopUpViaQRISResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TopUpViaQRISRequest(uuid = uuid, amount = amount)
            val response = repository.topUpViaQRIS(request = request, accessToken = accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}