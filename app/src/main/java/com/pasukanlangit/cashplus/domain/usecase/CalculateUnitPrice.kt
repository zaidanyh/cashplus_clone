package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.domain.model.UnitPriceResponse
import com.pasukanlangit.cashplus.model.request_body.CalculateUnitPriceRequestDto
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CalculateUnitPrice(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?, productCode: String, qty: String, dest: String, accessToken: String?
    ): Flow<DataState<UnitPriceResponse>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val requestCheck = TransactionRequest(uuid = uuid, kode_produk = "CEKDANA", dest = dest, pin = "")
            val check = repository.sendTransactionTAG(requestCheck, accessToken)
            val gson : Gson = GsonBuilder().create()
            if (check.isSuccessful) {
                lateinit var result: UnitPriceResponse
                check.body()?.let { data ->
                    result = UnitPriceResponse(name = data.billData.nama, "", "", "", "")
                }

                val request = CalculateUnitPriceRequestDto(uuid = uuid, productCode = productCode, qty = qty)
                val response = repository.calculateUnitPrice(request, accessToken)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        result = result.copy(
                            qty = data.qty, price = data.price, admin = data.admin, totalPrice = data.totalPrice
                        )
                    }
                    emit(DataState.data(data = result))
                    return@flow
                }

                try {
                    val errorBody = gson.fromJson(response.errorBody()?.string(), ErrorMessageResponse::class.java)
                    val message = errorBody.msg ?: errorBody.message ?: throw IOException()
                    emit(DataState.error(message))
                } catch (ioE: IOException) {
                    emit(DataState.error(ioE.message ?: Constants.unknownError))
                }
                return@flow
            }

            try {
                val errorBody = gson.fromJson(check.errorBody()?.string(), ErrorMessageResponse::class.java)
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