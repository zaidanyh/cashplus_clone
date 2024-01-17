package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class BillPayTransactionUseCase(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?, codeProduct: String, destination: String, pin: String,
        accessToken: String?, qty: String? = null, reqId: String? = null
    ): Flow<DataState<TransactionTAGResponse>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TransactionRequest(
                uuid = uuid, kode_produk = codeProduct,
                dest = destination, pin = pin, qty = qty, reqId = reqId
            )
            val response = repository.sendTransactionTAG(request, accessToken)
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
            emit(DataState.error(e.message ?: Constants.networkError))
        }
    }
}