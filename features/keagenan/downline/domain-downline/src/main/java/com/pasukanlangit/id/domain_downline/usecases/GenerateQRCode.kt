package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.GenerateQRRequest
import com.pasukanlangit.id.domain_downline.model.GenerateQRResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GenerateQRCode(
    private val repository: DownLineRepository
) {
    operator fun invoke(
       amount: String
    ): Flow<DataState<GenerateQRResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val request = GenerateQRRequest(
                amount = amount,
                uuid = uuid
            )
            val response = repository.generateQR(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}