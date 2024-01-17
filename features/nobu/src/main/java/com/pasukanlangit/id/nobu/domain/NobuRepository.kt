package com.pasukanlangit.id.nobu.domain

import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.domain.model.*

interface NobuRepository {

    suspend fun getProfile(request: NobuRequest, accessToken: String): ProfileResponse
    suspend fun getIsBinded(request: NobuRequest, accessToken: String): IsBindedResponse
    suspend fun getTermCondition(request: NobuRequest, accessToken: String): TermConditionResponse
    suspend fun getSecurityQuestion(request: NobuRequest, accessToken: String): List<SecurityQuestions>
    suspend fun accountCreation(request: AccountCreationRequest, accessToken: String): AccountCreationResponse
    suspend fun accountBinding(request: AccountBindingRequest, accessToken: String): AccountBindingResponse
    suspend fun verifyOtp(request: VerifyOtpRequest, accessToken: String): VerifyOtpResponse
    suspend fun balanceInquiry(request: NobuRequest, accessToken: String): BalanceInquiryResponse
    suspend fun getScanResult(request: SendResultScanRequest, accessToken: String): SendResultScanResponse
    suspend fun historyTrxNobu(request: HistoryTrxRequest, accessToken: String): HistoryTrxResponse
    suspend fun unbindAccount(request: NobuRequest, accessToken: String): UnbindResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}