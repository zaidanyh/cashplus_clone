package com.pasukanlangit.id.rebate.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.rebate.domain.RebateRepository
import com.pasukanlangit.id.rebate.domain.model.RebateRequest
import com.pasukanlangit.id.rebate.domain.model.RebateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRebate(
    private val repository: RebateRepository
){
    operator fun invoke(dateStart: String, dateEnd: String): Flow<DataState<RebateResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val request = RebateRequest(
                uuid = uuid,
                dateStart = dateStart,
                dateEnd = dateEnd
            )
            val response = repository.checkRebate(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}