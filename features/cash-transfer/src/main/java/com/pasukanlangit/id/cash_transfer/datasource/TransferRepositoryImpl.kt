package com.pasukanlangit.id.cash_transfer.datasource

import com.pasukanlangit.id.cash_transfer.datasource.network.TransferService
import com.pasukanlangit.id.cash_transfer.datasource.utils.*
import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.toUUIDDto

class TransferRepositoryImpl(
    private val service: TransferService,
    private val errorParser: GlobalErrorParser,
    private val sharedPref: SharedPrefDataSource
): TransferRepository {
    override suspend fun checkBalance(uuid: String, accessToken: String): CheckBalanceResponse {
        val request = UUIDRequest(uuid)
        val response = service.balanceCheck(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toCheckBalanceResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun localBankList(
        uuid: String,
        accessToken: String
    ): List<LocalBankListResponse> {
        val request = UUIDRequest(uuid)
        val response = service.localBankList(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toLocalBankListResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun savingLocalBank(
        request: BankRequest,
        accessToken: String
    ): BankStateResponse {
        val response = service.savingLocalBank(request.toSavingBankRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBankStateResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun deleteLocalBank(
        request: BankRequest,
        accessToken: String
    ): BankStateResponse {
        val response = service.localBankSavedDelete(request.toDeleteBankRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBankStateResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun localBankSaved(
        uuid: String,
        accessToken: String
    ): List<LocalBankSavedResponse> {
        val request = UUIDRequest(uuid)
        val response = service.localBankSaved(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toLocalBankSavedResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun transferTransaction(
        request: TransferTransactionRequest,
        accessToken: String
    ): TransferTransactionResponse {
        val response = service.transferTransaction(request.toTransferTransactionRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toTransferTransactionResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun fetchCountryList(
        request: FetchCountryRequest,
        accessToken: String
    ): List<FetchCountryResponse> {
        val response = service.countryList(request.toFetchCountryRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toListFetchCountryResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun globalBankByCountry(
        request: FetchBankByCountryRequest,
        accessToken: String
    ): List<FetchBankByCountryResponse> {
        val response = service.globalBankList(request.toFetchBankByCountryRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toListFetchBankByCountryResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun savingGlobalBank(
        request: SavingGlobalBankRequest,
        accessToken: String
    ): BankStateResponse {
        val response = service.savingGlobalBank(request.toSavingGlobalBankRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBankStateResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun globalBankSaved(
        request: UUIDRequest,
        accessToken: String
    ): List<GlobalBankSavedResponse> {
        val response = service.globalBankSaved(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toListGlobalBankSavedResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun deleteGlobalBank(
        request: BankRequest,
        accessToken: String
    ): BankStateResponse {
        val response = service.globalBankSavedDelete(request.toDeleteBankRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toBankStateResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun rateConversion(request: RateRequest, accessToken: String): RateResponse {
        val response = service.getRateConversion(request.toRateRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toRateResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun fetchRelationships(
        request: UUIDRequest,
        accessToken: String
    ): List<FetchRelationshipsResponse> {
        val response = service.getRelationships(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFetchRelationshipsResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun fetchNations(
        request: UUIDRequest,
        accessToken: String
    ): List<FetchNationResponse> {
        val response = service.getNations(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toFetchNationResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun globalCreateTransaction(
        request: GlobalBankCreateTransRequest,
        accessToken: String
    ): GlobalBankCreateTransResponse {
        val response = service.globalBankCreateTrans(request.toGlobalBankCreateTransRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toGlobalBankCreateTransResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()

    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}