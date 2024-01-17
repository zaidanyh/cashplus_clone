package com.pasukanlangit.id.kyc_domain.domain

import com.pasukanlangit.id.kyc_domain.domain.model.*
import com.pasukanlangit.id.library_core.domain.model.EKycCheckStatus
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest

interface KycRepository {
    suspend fun profileUser(request: UUIDRequest, accessToken: String): EKycProfileUser
    suspend fun eKycCheckStatus(request: UUIDRequest, accessToken: String): EKycCheckStatus
    suspend fun uploadEKycFile(request: EkycRequest, accessToken: String): EkycResponse
    suspend fun verifyingEKyc(request: EKycVerifyRequest, accessToken: String): EKycVerifyResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}