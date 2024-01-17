package com.pasukanlangit.cash_topup.datasource.network

import com.pasukanlangit.cash_topup.datasource.network.dto.*
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import retrofit2.Response
import retrofit2.http.*

interface TopUpServices {
    @POST("balance/check")
    suspend fun balanceCheck(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<BalanceResponseCore>
    @POST("balance/topup")
    suspend fun topUpViaBank(@Body request: TopUpViaBankRequestDto, @Header("x_access_token") accessToken: String): Response<TopUpViaBankResponseDto>

    @POST("nicepay/get_biaya")
    suspend fun costNicePay(@Body request: CostNicePayRequestDto, @Header("x_access_token") accessToken: String): Response<CostNicePayResponseDto>
    @POST("nicepay/va/registration")
    suspend fun topUpViaVirtualAccount(@Body request: TopUpNicePayRegistrationRequestDto, @Header("x_access_token") accessToken: String): Response<TopUpVAResponseDto>
    @POST("nicepay/cvs_ewallet/registration")
    suspend fun topUpViaEWallet(@Body request: TopUpNicePayRegistrationRequestDto, @Header("x_access_token") accessToken: String): Response<TopUpViaEWalletResponseDto>
    @POST("nicepay/qris/registration")
    suspend fun topUpViaQRIS(@Body request: TopUpViaQRISRequestDto, @Header("x_access_token") accessToken: String): Response<TopUpViaQRISResponseDto>

    @POST("flip_accept/list")
    suspend fun getViaFlipList(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<FlipAcceptListResponseDto>
    @POST("flip_accept/get_biaya")
    suspend fun getBillFlipAccept(@Body request: FlipAcceptRequestDto, @Header("x_access_token") accessToken: String): Response<CostFlipAcceptResponseDto>
    @POST("flip_accept/create_bill")
    suspend fun flipAcceptCreateBill(@Body request: FlipAcceptRequestDto, @Header("x_access_token") accessToken: String): Response<CreateBillFlipAcceptResponseDto>
}