package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toOmniPackageListResponse
import com.pasukanlangit.cashplus.data.toOmniRequestPackageListDto
import com.pasukanlangit.cashplus.domain.model.OmniPackageListResponse
import com.pasukanlangit.cashplus.domain.model.OmniRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class OmniPackageList(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?, dest: String, mlid: String, accessToken: String?
    ): Flow<DataState<OmniPackageListResponse>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = OmniRequest(uuid = uuid, dest = dest, mlid = mlid)
            val response = repository.omniPackageList(request.toOmniRequestPackageListDto(), accessToken)
            if (response.isSuccessful) {
                emit(DataState.data(data = response.body()?.toOmniPackageListResponse()))
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