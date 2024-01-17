package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toLoginOtpResponse
import com.pasukanlangit.cashplus.data.toResetPINRequestDto
import com.pasukanlangit.cashplus.domain.model.OtpResponse
import com.pasukanlangit.cashplus.domain.model.ResetPINRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ResetPINUseCase(
    private val repository: MainRepository
) {
    operator fun invoke(
        resetPINRequest: ResetPINRequest,
        accessToken: String
    ): Flow<DataState<OtpResponse>> = flow {
        emit(DataState.loading())
        try {
            val response = repository.changePinWithoutLastPin(resetPINRequest.toResetPINRequestDto(), accessToken)
            if (response.isSuccessful) {
                emit(DataState.data(data = response.body()?.toLoginOtpResponse()))
                return@flow
            }

            val gson : Gson = GsonBuilder().create()
            try {
                val errorBody = gson.fromJson(response.errorBody()?.string(), ErrorMessageResponse::class.java)
                val message = if (errorBody.rc == "30") "PIN Lama yang anda masukkan salah"
                    else errorBody.msg ?: errorBody.message ?: throw IOException()

                emit(DataState.error(message))
            } catch (ioE: IOException) {
                emit(DataState.error(ioE.message ?: Constants.unknownError))
            }
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}