package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.GoldPrice
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetGoldPrice(
    private val repository: CashGoldRepository
) {
    operator fun invoke(): Flow<DataState<GoldPrice>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val response = repository.getPriceGold(UUIDRequest(uuid = uuid), accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}