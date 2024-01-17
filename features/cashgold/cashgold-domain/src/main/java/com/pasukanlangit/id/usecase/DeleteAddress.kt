package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.DeleteAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteAddress(
    private val repository: CashGoldRepository
) {
    operator fun invoke(
        id: Int
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            val response = repository.deleteAddress(
                DeleteAddress(
                    uuid = uuid,
                    id = id
                )
                , accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}