package com.pasukanlangit.id.nobu.datasource.network

import com.pasukanlangit.id.nobu.datasource.network.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NobuServices {

    @POST("users/profile")
    suspend fun getProfile(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<ProfileResponseDto>

    @POST("nobu/cobrand/is_binded")
    suspend fun getIsBinded(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<IsBindedResponseDto>

    @POST("nobu/cobrand/get_term_condition")
    suspend fun getTermCondition(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<TermConditionResponseDto>

    @POST("nobu/cobrand/get_security_question")
    suspend fun getSecurityQuestion(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<SecurityQuestionResponseDto>

    @POST("nobu/cobrand/account_creation")
    suspend fun accountCreation(@Body request: AccountCreationRequestDto, @Header("x_access_token") accessToken: String): Response<AccountCreationResponseDto>

    @POST("nobu/cobrand/account_binding")
    suspend fun accountBinding(@Body request: AccountCreationRequestDto, @Header("x_access_token") accessToken: String): Response<AccountBindingResponseDTO>

    @POST("nobu/cobrand/verify_otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto, @Header("x_access_token") accessToken: String): Response<VerifyOtpResponseDTO>

    @POST("nobu/cobrand/balance_inquiry")
    suspend fun balanceInquiry(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<BalanceInquiryResponseDto>

    @POST("nobu/cobrand/qr_mpm")
    suspend fun getScanResult(@Body requestDto: SendResultScanRequestDto, @Header("x_access_token") accessToken: String): Response<QrMpmResponseDto>

    @POST("nobu/cobrand/trx_history")
    suspend fun historyTopUpNobu(@Body request: HistoryTrxRequestDto, @Header("x_access_token") accessToken: String): Response<HistoryTrxResponseDto>

    @POST("nobu/cobrand/unbind")
    suspend fun unbindAccount(@Body request: NobuRequestDto, @Header("x_access_token") accessToken: String): Response<IsBindedResponseDto>
}