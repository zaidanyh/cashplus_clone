package com.pasukanlangit.id.nobu.datasource

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.NobuServices
import com.pasukanlangit.id.nobu.datasource.network.dto.error.NobuErrorParser
import com.pasukanlangit.id.nobu.datasource.utils.*
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.*

@Suppress("BlockingMethodInNonBlockingContext")
class NobuRepositoryImpl(
    private val service: NobuServices,
    private val nobuErrorParser: NobuErrorParser,
    private val sharedPref: SharedPrefDataSource
): NobuRepository {

    override suspend fun getProfile(request: NobuRequest, accessToken: String): ProfileResponse {
        val response = service.getProfile(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toProfileResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getIsBinded(request: NobuRequest, accessToken: String): IsBindedResponse {
        val response = service.getIsBinded(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toIsBindedResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getTermCondition(request: NobuRequest, accessToken: String): TermConditionResponse {
        val response = service.getTermCondition(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toTermConditionResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getSecurityQuestion(request: NobuRequest, accessToken: String): List<SecurityQuestions> {
        val response = service.getSecurityQuestion(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toSecurityQuestions()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun accountCreation(request: AccountCreationRequest, accessToken: String): AccountCreationResponse {
        val response = service.accountCreation(request.toAccountCreationRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toAccountCreationResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun accountBinding(request: AccountBindingRequest, accessToken: String): AccountBindingResponse {
        val response = service.accountBinding(request.toAccountBindingRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toAccountBindingResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest, accessToken: String): VerifyOtpResponse {
        val response = service.verifyOtp(request.toVerifyOtpRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toVerifyResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun balanceInquiry(request: NobuRequest, accessToken: String): BalanceInquiryResponse {
        val response = service.balanceInquiry(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toBalanceInquiryResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getScanResult(request: SendResultScanRequest, accessToken: String): SendResultScanResponse {
        val response = service.getScanResult(request.toSendResultScanRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toSendResultScanResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun historyTrxNobu(request: HistoryTrxRequest, accessToken: String): HistoryTrxResponse {
        val response = service.historyTopUpNobu(request.toHistoryTrxRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toHistoryTrxResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun unbindAccount(request: NobuRequest, accessToken: String): UnbindResponse {
        val response = service.unbindAccount(request.toNobuRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toUnbindResponse()
            }
        }
        val error = nobuErrorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()
    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}