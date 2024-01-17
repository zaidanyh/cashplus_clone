package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.*
import com.pasukanlangit.id.library_core.domain.model.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LockGold(
    private val repository: CashGoldRepository
) {
    operator fun invoke(value: String?, goldPrice: Int?, lockGoldType: LockGoldType): Flow<DataState<LockGoldResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            if(value == null || goldPrice == null || goldPrice == 0){
                throw Exception(Constants.errorLockProcess)
            }

            val request = LockGoldRequest(
                uuid = uuid,
                nominal = value.replace(",", "."),
                priceGold = goldPrice
            )

            val response: LockGoldResponse = when(lockGoldType){
                LockGoldType.BY_GRAM -> {
                    repository.lockGoldByGram(request, accessToken)
                }
                LockGoldType.BY_PRICE -> {
                    repository.lockGoldByRupiah(request, accessToken)
                }
            }
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}