package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.NicePayRequest
import com.pasukanlangit.cash_topup.domain.model.TopUpViaVAResult
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ViaMerchantUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(bankMitraCode: String, payMethodCode: String, amt: String): Flow<DataState<TopUpViaVAResult>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val requestCost = NicePayRequest(
                uuid = uuid, bankMitraCode = bankMitraCode, payMethod = payMethodCode,
                amount = amt
            )
            val responseCost = repository.costNicePay(requestCost, accessToken)

            val amount = amt.toInt().plus(responseCost.cost.toIntOrNull() ?: 0)
            val requestMerchant = requestCost.copy(amt = "$amount")
            val responseMerchant = repository.topUpViaEWallet(requestMerchant, accessToken)
            val response = TopUpViaVAResult(adminCost = responseCost.cost, vaNumber = responseMerchant.payNo.toString())
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}