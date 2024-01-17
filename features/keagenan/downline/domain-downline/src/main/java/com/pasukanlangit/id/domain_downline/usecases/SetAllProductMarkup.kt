package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.SetAllProductMarkupRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SetAllProductMarkup(
    private val repository: DownLineRepository
) {

    operator fun invoke(
        dest: String,
        newMarkupValue: String
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(newMarkupValue.isEmpty()){
                throw Exception(Constants.markupEmpty)
            }
            if(newMarkupValue.toInt() < 0){
                throw Exception("Markup tidak boleh kurang dari 0")
            }
            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            val request = SetAllProductMarkupRequest(
                uuid = uuid, dest = dest, newMarkup = newMarkupValue
            )
            val response = repository.setAllProductsMarkup(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}