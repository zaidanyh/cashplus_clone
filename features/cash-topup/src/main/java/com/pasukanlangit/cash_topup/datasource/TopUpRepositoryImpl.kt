package com.pasukanlangit.cash_topup.datasource

import com.pasukanlangit.cash_topup.datasource.network.TopUpServices
import com.pasukanlangit.cash_topup.datasource.utils.*
import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.model.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.toUUIDDto

class TopUpRepositoryImpl(
    private val services: TopUpServices,
    private val errorParser: GlobalErrorParser,
    private val sharedPref: SharedPrefDataSource
): TopUpRepository {
    override suspend fun balanceCheck(request: UUIDRequest, accessToken: String): BalanceResponse {
        val response = services.balanceCheck(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBalanceResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun topUpViaBank(
        request: TopUpViaBankRequest,
        accessToken: String
    ): TopUpViaBankResponse {
        val response = services.topUpViaBank(request.toTopUpViaBankRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTopUpViaBankResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun costNicePay(
        request: NicePayRequest,
        accessToken: String
    ): CostNicePayResponse {
        val response = services.costNicePay(request.toBillNicePayRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toCostNicePayResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun topUpViaVirtualAccount(
        request: NicePayRequest,
        accessToken: String
    ): TopUpViaVAResponse {
        val response = services.topUpViaVirtualAccount(request.toTopUpNicePayVARequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTopUpViaVAResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun topUpViaEWallet(
        request: NicePayRequest,
        accessToken: String
    ): TopUpViaEWalletResponse {
        val response = services.topUpViaEWallet(request.toTopUpNicePayEWalletRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTopUpViaEWalletResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun topUpViaQRIS(
        request: TopUpViaQRISRequest,
        accessToken: String
    ): TopUpViaQRISResponse {
        val response = services.topUpViaQRIS(request.toTopUpViaQRISRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTopUpViaVAResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun flipAcceptList(
        request: UUIDRequest,
        accessToken: String
    ): List<FlipAcceptListResponse> {
        val response = services.getViaFlipList(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFlipAcceptListResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun costFlipAccept(
        request: FlipAcceptRequest,
        accessToken: String
    ): CostFlipAcceptResponse {
        val response = services.getBillFlipAccept(request.toFlipAcceptRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toCostFlipAcceptResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun createBillFlipAccept(
        request: FlipAcceptRequest,
        accessToken: String
    ): FlipAcceptCreateBillResponse {
        val response = services.flipAcceptCreateBill(request.toFlipAcceptRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFlipAcceptCreateBillResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()

    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}