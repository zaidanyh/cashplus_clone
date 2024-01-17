package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptCreateBillResponse
import com.pasukanlangit.cash_topup.domain.model.FlipAcceptRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FlipAcceptCreateBillUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(bankCode: String, amount: String): Flow<DataState<FlipAcceptCreateBillResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = FlipAcceptRequest(uuid = uuid, bankCode = bankCode, amount = amount)
            val responseCost = repository.costFlipAccept(request, accessToken)
            val amt = amount.toInt().plus(responseCost.cost.toInt())
            val requestCreateBill = request.copy(amount = "$amt")
            val responseCreateBill = repository.createBillFlipAccept(request = requestCreateBill, accessToken = accessToken)
            emit(DataState.data(data = responseCreateBill))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}