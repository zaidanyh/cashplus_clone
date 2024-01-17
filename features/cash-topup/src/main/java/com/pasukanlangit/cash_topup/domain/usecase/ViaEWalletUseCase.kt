package com.pasukanlangit.cash_topup.domain.usecase

import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.NicePayRequest
import com.pasukanlangit.cash_topup.domain.model.TopUpViaEWalletResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ViaEWalletUseCase(
    private val repository: TopUpRepository
) {
    operator fun invoke(
        bankMitraCode: String, payMethodCode: String,
        amt: String, billingPhone: String
    ): Flow<DataState<TopUpViaEWalletResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = NicePayRequest(
                uuid = uuid, bankMitraCode = bankMitraCode, payMethod = payMethodCode,
                amt = amt, billingPhone = billingPhone
            )
            val response = repository.topUpViaEWallet(request = request, accessToken = accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}