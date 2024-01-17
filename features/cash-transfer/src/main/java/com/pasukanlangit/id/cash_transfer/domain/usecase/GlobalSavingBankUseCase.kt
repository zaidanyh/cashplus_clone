package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.BankStateResponse
import com.pasukanlangit.id.cash_transfer.domain.model.SavingGlobalBankRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GlobalSavingBankUseCase(
    private val repository: TransferRepository
) {
    operator fun invoke(
        bankCode: String, accNum: String, accName: String,
        relationCode: String, nationCode: String, address: String,
        city: String
    ): Flow<DataState<BankStateResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = SavingGlobalBankRequest(
                uuid = uuid, bankCode = bankCode, bankAccNum = accNum,
                bankAccName = accName, relationCode = relationCode, nationCode = nationCode,
                addressStreet = address, addressCity = city
            )
            val response = repository.savingGlobalBank(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}