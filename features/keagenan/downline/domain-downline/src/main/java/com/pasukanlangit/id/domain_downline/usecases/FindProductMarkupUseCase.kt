package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DetailMarkupPlanRequest
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.domain_downline.model.ProductRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FindProductMarkupUseCase(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        codeMarkup: String,
        keyword: String,
        mainMarkup: String
    ): Flow<DataState<List<MarkupProductResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val productRequest = ProductRequest(uuid = uuid, keyword = keyword)
            val detailMarkupRequest = DetailMarkupPlanRequest(uuid = uuid, codeMarkupPlan = codeMarkup.replace(" ", "_"))

            val response = mutableListOf<Deferred<MarkupProductResponse>>()
            coroutineScope {
                val products = repository.getProductsList(productRequest, accessToken)
                val detailMarkup = repository.getDetailMarkupPlan(detailMarkupRequest, accessToken)

                products.forEach { product ->
                    val markup = async {
                        detailMarkup.detailMarkupPlan?.find { it.codeProduct == product.kodeProduct }
                    }
                    val result = async {
                        MarkupProductResponse(
                            kodeProduct = markup.await()?.codeProduct ?: product.kodeProduct,
                            kodeProvider = product.kodeProvider,
                            category = product.category,
                            price = product.price,
                            outlet_price = product.outlet_price,
                            isActive = product.isActive,
                            markup = markup.await()?.markup ?: "",
                            mainMarkup = mainMarkup
                        )
                    }
                    response.add(result)
                }
            }
            emit(DataState.data(data = response.awaitAll()))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}