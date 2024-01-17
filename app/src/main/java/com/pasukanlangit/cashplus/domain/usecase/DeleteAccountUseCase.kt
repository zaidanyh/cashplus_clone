package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toDeleteAccountRequestDto
import com.pasukanlangit.cashplus.domain.model.DeleteAccountRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class DeleteAccountUseCase(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String, reason: String, accessToken: String
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val request = DeleteAccountRequest(uuid = uuid, reason = reason)
            val response = repository.deleteAccount(request.toDeleteAccountRequestDto(), accessToken)
            if (response.isSuccessful) {
                emit(DataState.data(data = true))
                return@flow
            }

            val gson : Gson = GsonBuilder().create()
            try {
                val errorBody = gson.fromJson(response.errorBody()?.string(), ErrorMessageResponse::class.java)
                val message = errorBody.msg ?: errorBody.message ?: throw IOException()

                emit(DataState.error(message))
            } catch (ioE: IOException) {
                emit(DataState.error(ioE.message ?: Constants.unknownError))
            }
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}