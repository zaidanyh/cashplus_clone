package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.ResetMarkupRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ResetMarkup(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        markUpValue: String
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(markUpValue.isEmpty()){
                throw Exception(Constants.markupEmpty)
            }
            if(markUpValue.toInt() < 0){
                throw Exception("Markup tidak boleh kurang dari 0")
            }
            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val request = ResetMarkupRequest(
                uuid = uuid,
                markUpValue = markUpValue
            )
            val response = repository.resetMarkup(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}