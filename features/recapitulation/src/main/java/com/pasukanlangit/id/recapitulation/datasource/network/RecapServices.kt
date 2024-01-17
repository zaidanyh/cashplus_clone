package com.pasukanlangit.id.recapitulation.datasource.network

import com.pasukanlangit.id.recapitulation.datasource.network.dto.trans.*
import com.pasukanlangit.id.recapitulation.datasource.network.dto.deposit.*
import com.pasukanlangit.id.recapitulation.datasource.network.dto.RecapRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RecapServices {
    @POST("trx/profit_summary")
    suspend fun getProfitSummary(
        @Body request: RecapRequestDto, @Header("x_access_token") accessToken: String
    ): Response<RecapProfitSummaryResponseDto>
    @POST("trx/profit_by_product")
    suspend fun getProfitByProduct(
        @Body request: RecapRequestDto, @Header("x_access_token") accessToken: String
    ): Response<RecapProfitByProductResponseDto>

    @POST("balance/mutation")
    suspend fun getMutationBalance(
        @Body request: RecapRequestDto, @Header("x_access_token") accessToken: String
    ): Response<RecapDepositMutationResponseDto>
}