package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toTransactionBulkRequestDto
import com.pasukanlangit.cashplus.domain.model.DataInject
import com.pasukanlangit.cashplus.domain.model.TransactionBulkRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class TransactionBulk(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?, codeProduct: String, pin: String,
        dataBulk: List<DataInject>, accessToken: String?
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TransactionBulkRequest(uuid = uuid, codeProduct = codeProduct, pin = pin, data = dataBulk)
            val response = repository.transactionBulk(request.toTransactionBulkRequestDto(), accessToken)
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