package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.BankRequest
import com.pasukanlangit.id.cash_transfer.domain.model.BankStateResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteBankUseCase(
    private val repository: TransferRepository
) {
    operator fun invoke(
        bank_code: String, bank_acc_num: String, isGlobalBank: Boolean = false
    ): Flow<DataState<BankStateResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = BankRequest(uuid = uuid, bank_code = bank_code, bank_acc_num = bank_acc_num)
            val response = if (isGlobalBank) repository.deleteGlobalBank(request, accessToken)
                else repository.deleteLocalBank(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}