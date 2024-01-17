package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.LocationNameResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetProvinces(
    private val repository: DownLineRepository
) {
    operator fun invoke(): Flow<DataState<List<LocationNameResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID() ?: throw Exception(Constants.invalidAuth)

            val response = repository.getProvinces(uuid)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}