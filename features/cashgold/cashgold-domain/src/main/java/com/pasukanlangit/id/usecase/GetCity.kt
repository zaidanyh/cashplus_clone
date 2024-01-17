package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.model.Address
import com.pasukanlangit.id.model.LocationList
import com.pasukanlangit.id.model.LocationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCity(
    private val repository: CashGoldRepository
) {
    operator fun invoke(provinces: String): Flow<DataState<List<LocationList>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val response = repository.getCity(LocationRequest(uuid = uuid, name = provinces),
                accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}