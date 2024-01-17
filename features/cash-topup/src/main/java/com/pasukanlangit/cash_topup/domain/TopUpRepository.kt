package com.pasukanlangit.cash_topup.domain

import com.pasukanlangit.cash_topup.domain.model.*
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest

interface TopUpRepository {
    suspend fun balanceCheck(request: UUIDRequest, accessToken: String): BalanceResponse
    suspend fun topUpViaBank(request: TopUpViaBankRequest, accessToken: String): TopUpViaBankResponse

    suspend fun costNicePay(request: NicePayRequest, accessToken: String): CostNicePayResponse
    suspend fun topUpViaVirtualAccount(request: NicePayRequest, accessToken: String): TopUpViaVAResponse
    suspend fun topUpViaEWallet(request: NicePayRequest, accessToken: String): TopUpViaEWalletResponse
    suspend fun topUpViaQRIS(request: TopUpViaQRISRequest, accessToken: String): TopUpViaQRISResponse

    suspend fun flipAcceptList(request: UUIDRequest, accessToken: String): List<FlipAcceptListResponse>
    suspend fun costFlipAccept(request: FlipAcceptRequest, accessToken: String): CostFlipAcceptResponse
    suspend fun createBillFlipAccept(request: FlipAcceptRequest, accessToken: String): FlipAcceptCreateBillResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}