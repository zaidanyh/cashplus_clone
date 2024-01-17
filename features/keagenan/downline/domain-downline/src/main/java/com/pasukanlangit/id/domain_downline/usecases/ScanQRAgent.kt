package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.MessageResponse
import com.pasukanlangit.id.domain_downline.model.ScanQRAgenRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScanQRAgent(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        pin: String,
        id: String?
    ): Flow<DataState<MessageResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID() ?: throw Exception(Constants.invalidAuth)
            val accessToken = repository.getAccessToken() ?: throw Exception(Constants.invalidAuth)

            if(id.isNullOrEmpty()) throw Exception("QR Code tidak valid")

            val response = repository.scanQR(
                request = ScanQRAgenRequest(
                    pin = pin,
                    uuid = uuid,
                    id = id
                ),
                accessToken = accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}