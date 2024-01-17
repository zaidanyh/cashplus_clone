package com.pasukanlangit.id.rebate.datasource

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.rebate.datasource.network.RebateService
import com.pasukanlangit.id.rebate.datasource.utils.*
import com.pasukanlangit.id.rebate.domain.RebateRepository
import com.pasukanlangit.id.rebate.domain.model.*

@Suppress("BlockingMethodInNonBlockingContext")
class RebateRepositoryImpl(
   private val service: RebateService,
   private val errorParser: GlobalErrorParser,
   private val sharedPref: SharedPrefDataSource
): RebateRepository {

    override suspend fun checkRebate(request: RebateRequest, accessToken: String): RebateResponse {
        val response = service.checkRebate(request.toRebateRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toRebateResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkRebatePerProduct(
        request: RebateRequest,
        accessToken: String
    ): List<RebatePerProductResponse> {
        val response = service.checkRebateProductDetail(request.toRebateRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toRebateProductDetailList()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkRemainingRebate(
        request: RemainingRebateRequest,
        accessToken: String
    ): RemainingRebateResponse {
        val response = service.checkRemainingRebate(request.toRemainingRebateRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toRemainingRebateResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()

    override fun getAccessToken(): String? = sharedPref.getAccessToken()

}