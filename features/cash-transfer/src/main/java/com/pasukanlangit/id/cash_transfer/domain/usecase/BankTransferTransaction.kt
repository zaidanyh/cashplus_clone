package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionRequest
import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BankTransferTransaction(
    private val repository: TransferRepository
) {
    operator fun invoke(
        codeProduct: String, dest: String, pin: String, reqId: String?
    ): Flow<DataState<TransferTransactionResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TransferTransactionRequest(uuid, codeProduct, dest, pin, reqId)
            val response = repository.transferTransaction(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}