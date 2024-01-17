package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.NobuRequest
import com.pasukanlangit.id.nobu.domain.model.UnbindResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnBindAccountUseCase(
    private val repository: NobuRepository
) {
    operator fun invoke(): Flow<DataState<UnbindResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) throw Exception(Constants.invalidAuth)

            val response = repository.unbindAccount(NobuRequest(uuid), accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}