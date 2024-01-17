package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.*
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransferDepositDownLine(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        pin: String,
        dest: String?,
        nominal: String?
    ): Flow<DataState<MessageResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID() ?: throw Exception(Constants.invalidAuth)
            val accessToken = repository.getAccessToken() ?: throw Exception(Constants.invalidAuth)

            val response = repository.topUpDownLine(
                request = TopUpDownLine(
                    pin = pin,
                    dest = dest.toString(),
                    value = nominal?.replace(",", "").toString(),
                    uuid = uuid
                ),
                accessToken = accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}