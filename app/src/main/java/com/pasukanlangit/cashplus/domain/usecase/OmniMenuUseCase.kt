package com.pasukanlangit.cashplus.domain.usecase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.data.toListOmniMenuResponse
import com.pasukanlangit.cashplus.data.toOmniMenuRequestDto
import com.pasukanlangit.cashplus.data.toOmniPackageListResponse
import com.pasukanlangit.cashplus.data.toOmniRequestPackageListDto
import com.pasukanlangit.cashplus.domain.model.OmniMenuResponse
import com.pasukanlangit.cashplus.domain.model.OmniPackageListResponse
import com.pasukanlangit.cashplus.domain.model.OmniRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class OmniMenuUseCase(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String, dest: String, accessToken: String
    ): Flow<DataState<Pair<OmniMenuResponse?, OmniPackageListResponse?>>> = flow {
        emit(DataState.loading())
        try {
            val request = OmniRequest(uuid = uuid, dest = dest)
            val responseMenu = repository.omniMenu(request.toOmniMenuRequestDto(), accessToken)
            val gson : Gson = GsonBuilder().create()
            if (responseMenu.isSuccessful) {
                if (!responseMenu.body()?.error.isNullOrEmpty()) {
                    emit(
                        DataState.data(
                            data = Pair(
                                first = responseMenu.body()?.toListOmniMenuResponse(),
                                second = OmniPackageListResponse(emptyList(), emptyList(), "")
                            )
                        )
                    )
                    return@flow
                }
                val mlid = responseMenu.body()?.menu?.first()
                val requestPackage = OmniRequest(uuid = uuid, dest = dest, mlid = mlid?.mlid)
                val response = repository.omniPackageList(requestPackage.toOmniRequestPackageListDto(), accessToken)
                if (response.isSuccessful) {
                    responseMenu.body()?.let { menu ->
                        emit(
                            DataState.data(
                                data = Pair(
                                    menu.toListOmniMenuResponse(),
                                    response.body()?.toOmniPackageListResponse()
                                )
                            )
                        )
                    }
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
                val errorBody = gson.fromJson(responseMenu.errorBody()?.string(), ErrorMessageResponse::class.java)
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