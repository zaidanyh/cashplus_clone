package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.model.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddressList(
    private val repository: CashGoldRepository
) {
    operator fun invoke(): Flow<DataState<List<Address>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val response = repository.getAddressList(UUIDRequest(uuid = uuid), accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}