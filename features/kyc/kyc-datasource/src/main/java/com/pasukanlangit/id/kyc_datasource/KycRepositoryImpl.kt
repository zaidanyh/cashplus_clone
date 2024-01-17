package com.pasukanlangit.id.kyc_datasource

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.kyc_datasource.network.KycApiService
import com.pasukanlangit.id.kyc_domain.domain.KycRepository
import com.pasukanlangit.id.kyc_domain.domain.model.*
import com.pasukanlangit.id.library_core.datasource.utils.ErrorParser
import com.pasukanlangit.id.library_core.domain.model.EKycCheckStatus
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.toEKycCheckStatus
import com.pasukanlangit.id.library_core.utils.toUUIDDto

@Suppress("BlockingMethodInNonBlockingContext")
class KycRepositoryImpl(
    private val api: KycApiService,
    private val errorParser: ErrorParser,
    private val sharedPref: SharedPrefDataSource
): KycRepository {
    override suspend fun profileUser(request: UUIDRequest, accessToken: String): EKycProfileUser {
        val response = api.profileUsers(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toEKycProfileUser() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun eKycCheckStatus(request: UUIDRequest, accessToken: String): EKycCheckStatus {
        val response = api.eKycCheckStatus(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toEKycCheckStatus() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun uploadEKycFile(request: EkycRequest, accessToken: String): EkycResponse {
        val response = api.uploadFile(request.toUploadFileEKycRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toEKycResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun verifyingEKyc(request: EKycVerifyRequest, accessToken: String): EKycVerifyResponse {
        val response = api.verifyEKyc(request.toEKycVerifyRequestDto(), accessToken)
        if (response.isSuccessful) response.body()?.let { data -> return data.toEKycVerifyResponse() }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()
    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}