package com.pasukanlangit.id.cash_transfer.datasource.network

import com.pasukanlangit.id.cash_transfer.datasource.network.dto.*
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TransferService {
    @POST("balance/check")
    suspend fun balanceCheck(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<BalanceResponseCore>

    @POST("product/bank_list")
    suspend fun localBankList(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<LocalBankListResponseDto>
    @POST("users/banks/add")
    suspend fun savingLocalBank(@Body request: SavingLocalBankRequestDto, @Header("x_access_token") accessToken: String): Response<ErrorMessageResponse>
    @POST("users/banks/list")
    suspend fun localBankSaved(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<LocalBankSavedResponseDto>
    @POST("users/banks/delete")
    suspend fun localBankSavedDelete(@Body request: DeleteBankRequestDto, @Header("x_access_token") accessToken: String): Response<ErrorMessageResponse>
    @POST("trx/send")
    suspend fun transferTransaction(@Body request: TransferTransactionRequestDto, @Header("x_access_token") accessToken: String): Response<TransferTransactionResponseDto>

    @POST("product/fbanks/country_list")
    suspend fun countryList(@Body request: FetchCountryRequestDto, @Header("x_access_token") accessToken: String): Response<FetchCountryResponseDto>
    @POST("product/fbanks/list")
    suspend fun globalBankList(@Body request: FetchBankByCountryRequestDto, @Header("x_access_token") accessToken: String): Response<FetchBankByCountryResponseDto>
    @POST("users/fbanks/add")
    suspend fun savingGlobalBank(@Body request: SavingGlobalBankRequestDto, @Header("x_access_token") accessToken: String): Response<ErrorMessageResponse>
    @POST("users/fbanks/list")
    suspend fun globalBankSaved(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<GlobalBankSavedResponseDto>
    @POST("users/fbanks/delete")
    suspend fun globalBankSavedDelete(@Body request: DeleteBankRequestDto, @Header("x_access_token") accessToken: String): Response<ErrorMessageResponse>
    @POST("fbanks/get_rate")
    suspend fun getRateConversion(@Body request: RateRequestDto, @Header("x_access_token") accessToken: String): Response<RateResponseDto>
    @POST("fbanks/relationships")
    suspend fun getRelationships(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<FetchRelationshipsResponseDto>
    @POST("fbanks/nation")
    suspend fun getNations(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<FetchNationResponseDto>
    @POST("fbanks/create_trans")
    suspend fun globalBankCreateTrans(@Body request: GlobalBankCreateTransRequestDto, @Header("x_access_token") accessToken: String): Response<GlobalBankCreateTransResponseDto>
}