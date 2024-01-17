package com.pasukanlangit.id.rebate.domain

import com.pasukanlangit.id.rebate.domain.model.*

interface RebateRepository {
    suspend fun checkRebate(request: RebateRequest, accessToken: String): RebateResponse
    suspend fun checkRebatePerProduct(request: RebateRequest, accessToken: String): List<RebatePerProductResponse>
    suspend fun checkRemainingRebate(request: RemainingRebateRequest, accessToken: String): RemainingRebateResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}