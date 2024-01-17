package com.pasukanlangit.id.kyc_datasource.network

import com.pasukanlangit.id.kyc_datasource.network.dto.*
import com.pasukanlangit.id.library_core.datasource.network.dto.EKycCheckStatusResponseDto
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface KycApiService {

    @POST("users/profile")
    suspend fun profileUsers(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<ProfileResponseDto>

    @POST("users/ekyc/status")
    suspend fun eKycCheckStatus(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<EKycCheckStatusResponseDto>

    @POST("users/ekyc/upload")
    suspend fun uploadFile(@Body dtoRequest: UploadFileEKycRequestDto, @Header("x_access_token") accessToken: String): Response<UploadFileEKycResponseDto>

    @POST("users/ekyc/verify")
    suspend fun verifyEKyc(@Body dtoRequest: VerifyEKycRequestDto, @Header("x_access_token") accessToken: String): Response<VerifyEKycResponseDto>
}