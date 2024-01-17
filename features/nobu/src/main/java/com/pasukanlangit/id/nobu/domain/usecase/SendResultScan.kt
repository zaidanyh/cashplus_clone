package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.SendResultScanRequest
import com.pasukanlangit.id.nobu.domain.model.SendResultScanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendResultScan(
    private val repository: NobuRepository
) {
    operator fun invoke(resultScan: String): Flow<DataState<SendResultScanResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            val request = SendResultScanRequest(
                uuid = uuid,
                resultScan = resultScan,
                callBackUrl = Constants.deepLinkPrefix
//                callBackUrl = "intent://open/#Intent;scheme=https;package=com.pasukanlangit.cashplus;end"
            )
            val response = repository.getScanResult(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}