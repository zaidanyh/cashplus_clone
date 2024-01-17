package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toPrintReceiptResponse
import com.pasukanlangit.cashplus.data.toUpdateSellingPriceRequestDto
import com.pasukanlangit.cashplus.domain.model.PrintReceiptResponse
import com.pasukanlangit.cashplus.domain.model.UpdateSellingPriceRequest
import com.pasukanlangit.cashplus.model.request_body.PrintReceiptRequestDto
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class PrintReceiptUseCase(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?, productCode: String, trxId: String, adjustPrice: String, accessToken: String?
    ): Flow<DataState<PrintReceiptResponse>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val updateRequest = UpdateSellingPriceRequest(uuid = uuid, productCode = productCode, newPrice = adjustPrice)
            val updateResponse = repository.updateSellingPrice(request = updateRequest.toUpdateSellingPriceRequestDto(), accessToken = accessToken)
            val gson : Gson = GsonBuilder().create()
            if (updateResponse.isSuccessful) {
                val request = PrintReceiptRequestDto(uuid = uuid, trxId = trxId, adjustPrice = adjustPrice)
                val response = repository.newPrintReceipt(requestDto = request, accessToken = accessToken)

                if (response.isSuccessful) {
                    emit(DataState.data(data = response.body()?.toPrintReceiptResponse()))
                    return@flow
                }

                try {
                    val errorBody = gson.fromJson(response.errorBody()?.string(), ErrorMessageResponse::class.java)
                    val message = errorBody.message ?: errorBody.msg ?: throw IOException()

                    emit(DataState.error(message))
                } catch (e: IOException) {
                    emit(DataState.error(e.message ?: Constants.networkError))
                }
                return@flow
            }

            try {
                val errorBody = gson.fromJson(updateResponse.errorBody()?.string(), ErrorMessageResponse::class.java)
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