package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.CostNicePayResponse
import com.pasukanlangit.cash_topup.domain.model.NicePayRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CostNicePayUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(bankMitraCode: String, pay_method: String, amount: String): Flow<DataState<CostNicePayResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = NicePayRequest(uuid = uuid, bankMitraCode = bankMitraCode, payMethod = pay_method, amount = amount)
            val response = repository.costNicePay(request = request, accessToken = accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}