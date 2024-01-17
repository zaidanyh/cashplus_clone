package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetProfile(
    private val repository: MainRepository
) {
    operator fun invoke(uuid: String, accessToken: String): Flow<DataState<ProfileResponse>> = flow {
        emit(DataState.loading())
        try {
            val request = ProfileRequest(uuid)
            val response = repository.getProfile(profileRequest = request, accessToken = accessToken)
            if (response.isSuccessful) {
                emit(DataState.data(data = response.body()))
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