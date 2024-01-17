package com.pasukanlangit.id.rebate.datasource.network

import com.pasukanlangit.id.rebate.datasource.network.dto.RebateProductDetailResponseDto
import com.pasukanlangit.id.rebate.datasource.network.dto.RebateRequestDto
import com.pasukanlangit.id.rebate.datasource.network.dto.RebateResponseDto
import com.pasukanlangit.id.rebate.datasource.network.dto.RemainingRebateRequestDto
import com.pasukanlangit.id.rebate.datasource.network.dto.RemainingRebateResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RebateService {
    @POST("users/check_rebate")
    suspend fun checkRebate(@Body requestDto: RebateRequestDto, @Header("x_access_token") token: String): Response<RebateResponseDto>

    @POST("users/check_rebate_per_product")
    suspend fun checkRebateProductDetail(@Body requestDto: RebateRequestDto, @Header("x_access_token") token: String): Response<RebateProductDetailResponseDto>

    @POST("users/check_remaining_rebate")
    suspend fun checkRemainingRebate(@Body remainingRequest: RemainingRebateRequestDto, @Header("x_access_token") token: String): Response<RemainingRebateResponseDto>
}