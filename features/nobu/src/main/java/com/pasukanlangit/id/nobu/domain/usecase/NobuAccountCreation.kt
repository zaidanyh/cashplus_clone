package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.AccountCreationRequest
import com.pasukanlangit.id.nobu.domain.model.AccountCreationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NobuAccountCreation(
    private val repository: NobuRepository
) {
    operator fun invoke(
        phone: String,
        email: String,
        name: String,
        securityAnswer: String?,
        securityQuestion: String?,
        securityCode: String?
    ): Flow<DataState<AccountCreationResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if (uuid == null || accessToken == null) {
                throw Exception(Constants.invalidAuth)
            }

            val request = AccountCreationRequest(
                uuid = uuid, phone = phone, email = email, name = name,
                securityAnswer = securityAnswer, securityQuestion = securityQuestion,
                securityCode = securityCode,
                redirectUrl = "_blank"
            )
            val response = repository.accountCreation(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}