package com.pasukanlangit.id.cash_transfer.datasource.utils

import com.pasukanlangit.id.cash_transfer.datasource.network.dto.*
import com.pasukanlangit.id.cash_transfer.domain.model.*
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore

fun BalanceResponseCore.toCheckBalanceResponse(): CheckBalanceResponse =
    CheckBalanceResponse(balance = this.balance)

fun LocalBankListResponseDto.toLocalBankListResponse(): List<LocalBankListResponse> =
    this.data?.map { bank ->
        LocalBankListResponse(
            bankCode = bank.bank_code, bankName = bank.bank_name,
            biFastCode = bank.biFastCode, imgBank = bank.imgBank
        )
    } ?: emptyList()

fun BankRequest.toSavingBankRequestDto(): SavingLocalBankRequestDto =
    SavingLocalBankRequestDto(
        uuid = this.uuid, bank_code = this.bank_code, bank_account_number = this.bank_acc_num,
        bank_account_name = this.bank_acc_name.toString()
    )

fun BankRequest.toDeleteBankRequestDto(): DeleteBankRequestDto =
    DeleteBankRequestDto(
        uuid = this.uuid, bank_code = this.bank_code, bank_account_num = this.bank_acc_num
    )

fun ErrorMessageResponse.toBankStateResponse(): BankStateResponse =
    BankStateResponse(message = (this.msg ?: this.message).toString())

fun LocalBankSavedResponseDto.toLocalBankSavedResponse(): List<LocalBankSavedResponse> =
    this.listData?.map { dataBank ->
        LocalBankSavedResponse(
            bank_code = dataBank.bank_code, bank_name = dataBank.bank_name,
            bank_acc_name = dataBank.bank_account_name, bank_acc_num = dataBank.bank_account_number,
            bank_img = dataBank.img_url
        )
    } ?: emptyList()

fun TransferTransactionRequest.toTransferTransactionRequestDto(): TransferTransactionRequestDto =
    TransferTransactionRequestDto(
        uuid = this.uuid, productCode = this.codeProduct, dest = this.dest,
        pin = this.pin, reqId = this.reqId
    )

fun TransferTransactionResponseDto.toTransferTransactionResponse(): TransferTransactionResponse =
    TransferTransactionResponse(
        fee = this.fee ?: "", price = this.price ?: "",
        transferBillData = TransferBillData(
            bank_acc_num = this.billData?.noRek, name = this.billData?.nama,
            bankName = this.billData?.bank, bill = this.billData?.tagihan,
            adminFee = this.billData?.admin, total = this.billData?.total
        )
    )

fun FetchCountryRequest.toFetchCountryRequestDto(): FetchCountryRequestDto =
    FetchCountryRequestDto(
        uuid = this.uuid, countryName = this.country_name, currencyDesc = this.currency_dsc
    )

fun FetchCountryResponseDto.toListFetchCountryResponse(): List<FetchCountryResponse> =
    this.data?.map { data ->
        FetchCountryResponse(
            codeCountry = data.codeCountry, nameCountry = data.nameCountry, currency = data.currency,
            currencyDsc = data.currencyDesc, imgUrl = data.imgUrl
        )
    } ?: emptyList()

fun FetchBankByCountryRequest.toFetchBankByCountryRequestDto(): FetchBankByCountryRequestDto =
    FetchBankByCountryRequestDto(
        uuid = this.uuid, codeCountry = this.countryCode
    )

fun FetchBankByCountryResponseDto.toListFetchBankByCountryResponse(): List<FetchBankByCountryResponse> =
    this.data?.map { data ->
        FetchBankByCountryResponse(
            bankCode = data.codeBank, codeCountry = data.codeCountry, codeSwift = data.codeSwift,
            bankName = data.bankName, imgUrl = data.imgUrl
        )
    } ?: emptyList()

fun GlobalBankSavedResponseDto.toListGlobalBankSavedResponse(): List<GlobalBankSavedResponse> =
    this.savedList?.map { data ->
        GlobalBankSavedResponse(
            bankCode = data.codeBank, countryCode = data.codeCountry, relationship = data.relationship,
            nationCode = data.nationCode, address = data.addressStreet, city = data.addressCity,
            currency = data.currency, currencyDsc = data.currencyDesc, countryName = data.nameCountry,
            bankName = data.bankName, bankAccNum = data.accountNumber, bankAccName = data.accountName,
            imgUrl = data.imgUrl, countryImgUrl = data.countryImgUrl
        )
    } ?: emptyList()

fun SavingGlobalBankRequest.toSavingGlobalBankRequestDto(): SavingGlobalBankRequestDto =
    SavingGlobalBankRequestDto(
        uuid = this.uuid, bank_code = this.bankCode, bank_account_number = this.bankAccNum,
        bank_account_name = this.bankAccName, relationshipCode = this.relationCode, nationCode = this.nationCode,
        addressStreet = this.addressStreet, addressCity = this.addressCity
    )

fun RateRequest.toRateRequestDto(): RateRequestDto =
    RateRequestDto(uuid = this.uuid, currency = this.currency, amount = this.amount)

fun RateResponseDto.toRateResponse(): RateResponse =
    RateResponse(
        quoteId = this.quoteId, rate = this.rate, usdIdrRate = this.usdIdrRate, kursMarkup = this.kursMarkup,
        adminTotal = this.adminTotal, amountSend = this.idrAmountSend, idrAmount = this.idrAmount
    )

fun FetchRelationshipsResponseDto.toFetchRelationshipsResponse(): List<FetchRelationshipsResponse> =
    this.data?.map { data ->
        FetchRelationshipsResponse(code = data.codeRelation, desc = data.desc)
    } ?: emptyList()

fun FetchNationResponseDto.toFetchNationResponse(): List<FetchNationResponse> =
    this.data?.map { nation ->
        FetchNationResponse(nationCode = nation.nationCode, nationName = nation.nationName)
    } ?: emptyList()

fun GlobalBankCreateTransRequest.toGlobalBankCreateTransRequestDto(): GlobalBankCreateTransRequestDto =
    GlobalBankCreateTransRequestDto(
        uuid = this.uuid, bxQuoteId = this.quoteId, relationshipCode = this.relationCode,
        bankCode = this.bankCode, bankAccNum = this.bankAccNum, countryCode = this.countryCode,
        firstName = this.firstName, lastName = this.lastName, addressStreet = this.addressStreet,
        addressCity = this.addressCity, nationCode = this.nationCode
    )

fun GlobalBankCreateTransResponseDto.toGlobalBankCreateTransResponse(): GlobalBankCreateTransResponse =
    GlobalBankCreateTransResponse(quoteId = this.quoteId)