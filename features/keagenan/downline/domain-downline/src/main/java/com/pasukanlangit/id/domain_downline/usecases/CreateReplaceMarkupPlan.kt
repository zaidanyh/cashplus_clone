package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.CreateReplaceMarkupPlanRequest
import com.pasukanlangit.id.domain_downline.model.DetailMarkupPlanRequest
import com.pasukanlangit.id.domain_downline.model.ProductMarkup
import com.pasukanlangit.id.domain_downline.utils.toListProductMarkup
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateReplaceMarkupPlan(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        markupCode: String, dsc: String,
        products: List<ProductMarkup>? = null, isAddProduct: Boolean = false
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val replacingMarkupCode = markupCode.replace(" ", "_")
            if (isAddProduct) {
                val detailMarkupReq = DetailMarkupPlanRequest(uuid = uuid, codeMarkupPlan = replacingMarkupCode)
                val responseDetailMarkup = repository.getDetailMarkupPlan(detailMarkupReq, accessToken)
                val detailMarkups = responseDetailMarkup.detailMarkupPlan?.toListProductMarkup()?.toMutableList()
                if (!products.isNullOrEmpty())
                    products.forEach { newData ->
                        detailMarkups?.add(newData)
                    }
                val detailMarkup = detailMarkups?.toList()

                val request = CreateReplaceMarkupPlanRequest(
                    uuid = uuid, markupCode = replacingMarkupCode,
                    description = dsc, data = detailMarkup as List<ProductMarkup>
                )
                val response = repository.createReplaceMarkupPlan(request, accessToken)
                emit(DataState.data(data = response))
                return@flow
            }

            val request = CreateReplaceMarkupPlanRequest(
                uuid = uuid, markupCode = replacingMarkupCode, description = dsc,
                data = products ?: emptyList()
            )
            val response = repository.createReplaceMarkupPlan(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}