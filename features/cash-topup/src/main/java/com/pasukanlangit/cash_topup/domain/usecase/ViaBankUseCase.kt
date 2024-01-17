package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.TopUpViaBankRequest
import com.pasukanlangit.cash_topup.domain.model.TopUpViaBankResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ViaBankUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(bankName: String, value: String): Flow<DataState<TopUpViaBankResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TopUpViaBankRequest(uuid = uuid, bankName = bankName, value = value)
            val response = repository.topUpViaBank(request = request, accessToken = accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}