package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.MarkupPlan
import com.pasukanlangit.id.domain_downline.model.PricePlanRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMarkupPlan(
    private val repository: DownLineRepository
) {
    operator fun invoke(keyword: String? = null): Flow<DataState<List<MarkupPlan>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = PricePlanRequest(uuid, keyword)
            val response = repository.getListMarkupPlans(request, accessToken)
            emit(DataState.data(data = response.markupPlans))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}