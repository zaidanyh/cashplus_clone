package com.pasukanlangit.id.recapitulation.datasource

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.recapitulation.datasource.network.RecapServices
import com.pasukanlangit.id.recapitulation.datasource.utils.toMutationDepositResponse
import com.pasukanlangit.id.recapitulation.datasource.utils.toProfitByProductResponse
import com.pasukanlangit.id.recapitulation.datasource.utils.toProfitSummaryResponse
import com.pasukanlangit.id.recapitulation.datasource.utils.toRecapRequestDto
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse
import com.pasukanlangit.id.recapitulation.domain.model.ProfitSummaryResponse
import com.pasukanlangit.id.recapitulation.domain.model.RecapRequest

class RecapRepositoryImpl(
    private val service: RecapServices,
    private val errorParser: GlobalErrorParser,
    private val sharedPref: SharedPrefDataSource
): RecapRepository {
    override suspend fun getProfitSummary(request: RecapRequest, accessToken: String): ProfitSummaryResponse {
        val response = service.getProfitSummary(request.toRecapRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toProfitSummaryResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getProfitByProduct(request: RecapRequest, accessToken: String): List<ProfitByProductResponse> {
        val response = service.getProfitByProduct(request.toRecapRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toProfitByProductResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getMutationBalance(request: RecapRequest, accessToken: String): List<MutationDepositResponse> {
        val response = service.getMutationBalance(request.toRecapRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toMutationDepositResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()
    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}