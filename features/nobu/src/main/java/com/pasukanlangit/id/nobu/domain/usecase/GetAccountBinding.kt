package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.AccountBindingRequest
import com.pasukanlangit.id.nobu.domain.model.AccountBindingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAccountBinding(
    private val repository: NobuRepository
) {
    operator fun invoke(
        phone: String,
        email: String,
        name: String,
    ): Flow<DataState<AccountBindingResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if (uuid == null || accessToken == null) {
                throw Exception(Constants.invalidAuth)
            }

            val request = AccountBindingRequest(
                uuid = uuid, phone = phone, email = email, name = name,
                redirectUrl = Constants.deepLinkPrefix
//                redirectUrl = "intent://open/#Intent;scheme=https;package=com.pasukanlangit.cashplus;end"
            )
            val response = repository.accountBinding(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}