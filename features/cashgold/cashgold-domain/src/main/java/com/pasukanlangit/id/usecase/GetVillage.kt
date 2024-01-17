package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.LocationVillageRequest
import com.pasukanlangit.id.model.VillageList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetVillage(
    private val repository: CashGoldRepository
) {
    operator fun invoke(city: String, district: String): Flow<DataState<List<VillageList>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val response = repository.getVillage(LocationVillageRequest(uuid = uuid, city = city, district = district),
                accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}