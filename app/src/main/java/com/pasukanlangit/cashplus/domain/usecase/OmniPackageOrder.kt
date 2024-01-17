package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toOmniPackageOrderResponse
import com.pasukanlangit.cashplus.data.toOmniRequestPackageOrderDto
import com.pasukanlangit.cashplus.domain.model.OmniPackageOrderResponse
import com.pasukanlangit.cashplus.domain.model.OmniRequest
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class OmniPackageOrder(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String,
        dest: String,
        packageId: String,
        accessToken: String
    ): Flow<DataState<OmniPackageOrderResponse>> = flow {
        emit(DataState.loading())
        try {
            val request = OmniRequest(uuid = uuid, dest = dest, packageId = packageId)
            val response = repository.omniPackageOrder(request.toOmniRequestPackageOrderDto(), accessToken)
            val gson : Gson = GsonBuilder().create()
            if (response.isSuccessful) {
                val paymentCode = response.body()?.paymentCode.toString()

                val requestTAG = TransactionRequest(uuid = uuid, kode_produk = "TAGOMNICHANNEL", dest = "$paymentCode-$dest", pin = "")
                val responseTAG = repository.sendTransactionTAG(requestTAG, accessToken)
                if (responseTAG.isSuccessful) {
                    emit(DataState.data(data = responseTAG.body()?.toOmniPackageOrderResponse(paymentCode)))
                    return@flow
                }

                try {
                    val errorBody = gson.fromJson(responseTAG.errorBody()?.string(), ErrorMessageResponse::class.java)
                    val message = errorBody.msg ?: errorBody.message ?: throw IOException()
                    emit(DataState.error(message))
                } catch (e: IOException) {
                    emit(DataState.error(e.message ?: Constants.networkError))
                }
                return@flow
            }

            try {
                val errorBody = gson.fromJson(response.errorBody()?.string(), ErrorMessageResponse::class.java)
                val message = errorBody.message ?: errorBody.msg ?: throw IOException()

                emit(DataState.error(message))
            } catch (ioE: IOException) {
                emit(DataState.error(ioE.message ?: Constants.networkError))
            }
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}