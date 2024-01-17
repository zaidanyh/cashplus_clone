package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.NobuRequest
import com.pasukanlangit.id.nobu.domain.model.TermConditionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTermCondition(
    private val repository: NobuRepository
) {
    operator fun invoke(): Flow<DataState<TermConditionResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if (uuid == null || accessToken == null) {
                throw Exception(Constants.invalidAuth)
            }

            val response = repository.getTermCondition(NobuRequest(uuid), accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}