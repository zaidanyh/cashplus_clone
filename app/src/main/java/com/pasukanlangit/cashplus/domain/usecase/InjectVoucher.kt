package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class InjectVoucher(
    private val repository: MainRepository
) {
    operator fun invoke(uuid: String?, accessToken: String?): Flow<DataState<List<ProductModel>>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = ProductRequest(
                uuid = uuid, category = "", kode_provider = "",
                is_faktur = "", is_active = "1", is_bulk = "1"
            )
            val response = repository.getProductList(productRequest = request, accessToken = accessToken)
            if (response.isSuccessful) {
                emit(DataState.data(data = response.body()?.data))
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