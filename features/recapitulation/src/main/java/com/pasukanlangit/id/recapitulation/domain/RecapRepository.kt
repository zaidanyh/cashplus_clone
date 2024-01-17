package com.pasukanlangit.id.recapitulation.domain

import com.pasukanlangit.id.recapitulation.domain.model.*

interface RecapRepository {
    suspend fun getProfitSummary(
        request: RecapRequest, accessToken: String
    ): ProfitSummaryResponse
    suspend fun getProfitByProduct(
        request: RecapRequest, accessToken: String
    ): List<ProfitByProductResponse>
    suspend fun getMutationBalance(
        request: RecapRequest, accessToken: String
    ): List<MutationDepositResponse>

    fun getUUID(): String?
    fun getAccessToken(): String?
}