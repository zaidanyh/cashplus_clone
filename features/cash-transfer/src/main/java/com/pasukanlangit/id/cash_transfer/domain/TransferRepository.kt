package com.pasukanlangit.id.cash_transfer.domain

import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest

interface TransferRepository {
    suspend fun checkBalance(uuid: String, accessToken: String): CheckBalanceResponse
    suspend fun localBankList(uuid: String, accessToken: String): List<LocalBankListResponse>
    suspend fun savingLocalBank(request: BankRequest, accessToken: String): BankStateResponse
    suspend fun deleteLocalBank(request: BankRequest, accessToken: String): BankStateResponse
    suspend fun localBankSaved(uuid: String, accessToken: String): List<LocalBankSavedResponse>
    suspend fun transferTransaction(request: TransferTransactionRequest, accessToken: String): TransferTransactionResponse

    suspend fun fetchCountryList(request: FetchCountryRequest, accessToken: String): List<FetchCountryResponse>
    suspend fun globalBankByCountry(request: FetchBankByCountryRequest, accessToken: String): List<FetchBankByCountryResponse>
    suspend fun savingGlobalBank(request: SavingGlobalBankRequest, accessToken: String): BankStateResponse
    suspend fun globalBankSaved(request: UUIDRequest, accessToken: String): List<GlobalBankSavedResponse>
    suspend fun deleteGlobalBank(request: BankRequest, accessToken: String): BankStateResponse
    suspend fun rateConversion(request: RateRequest, accessToken: String): RateResponse
    suspend fun fetchRelationships(request: UUIDRequest, accessToken: String): List<FetchRelationshipsResponse>
    suspend fun fetchNations(request: UUIDRequest, accessToken: String): List<FetchNationResponse>
    suspend fun globalCreateTransaction(request: GlobalBankCreateTransRequest, accessToken: String): GlobalBankCreateTransResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}