package com.pasukanlangit.cash_topup.datasource.utils

import com.pasukanlangit.cash_topup.datasource.network.dto.*
import com.pasukanlangit.cash_topup.domain.model.*
import com.pasukanlangit.id.core.model.BalanceResponseCore

fun BalanceResponseCore.toBalanceResponse(): BalanceResponse = BalanceResponse(this.balance)

fun TopUpViaBankRequest.toTopUpViaBankRequestDto(): TopUpViaBankRequestDto =
    TopUpViaBankRequestDto(
        uuid = this.uuid, bank_name = this.bankName, value = this.value
    )

fun TopUpViaBankResponseDto.toTopUpViaBankResponse(): TopUpViaBankResponse =
    TopUpViaBankResponse(
        amount = this.amount, uniqueId = this.uniqueId, bankName = this.bank_name,
        bankAccNum = this.bankAccNumber, bankAccName = this.bankAccName
    )

fun NicePayRequest.toBillNicePayRequestDto(): CostNicePayRequestDto =
    CostNicePayRequestDto(
        uuid = this.uuid, bankMitraCode = this.bankMitraCode, payMethodCode = this.payMethod,
        amount = this.amount?.toInt() ?: 0
    )

fun CostNicePayResponseDto.toCostNicePayResponse(): CostNicePayResponse =
    CostNicePayResponse(cost = this.bill)

fun NicePayRequest.toTopUpNicePayVARequestDto(): TopUpNicePayRegistrationRequestDto =
    TopUpNicePayRegistrationRequestDto(
        uuid = this.uuid, bankMitraCode = this.bankMitraCode, payMethodCode = this.payMethod,
        amount = this.amt.toString()
    )

fun TopUpVAResponseDto.toTopUpViaVAResponse(): TopUpViaVAResponse =
    TopUpViaVAResponse(vaNumber = this.vaNumber)

fun NicePayRequest.toTopUpNicePayEWalletRequestDto(): TopUpNicePayRegistrationRequestDto =
    TopUpNicePayRegistrationRequestDto(
        uuid = this.uuid, bankMitraCode = this.bankMitraCode, payMethodCode = this.payMethod,
        amount = this.amt.toString(), billingPhone = this.billingPhone
    )

fun TopUpViaEWalletResponseDto.toTopUpViaEWalletResponse(): TopUpViaEWalletResponse =
    TopUpViaEWalletResponse(message = this.message, payNo = this.pay_number)

fun TopUpViaQRISRequest.toTopUpViaQRISRequestDto(): TopUpViaQRISRequestDto =
    TopUpViaQRISRequestDto(uuid = this.uuid, amount = this.amount)

fun TopUpViaQRISResponseDto.toTopUpViaVAResponse(): TopUpViaQRISResponse =
    TopUpViaQRISResponse(qrUrl = this.qrUrl.toString())

fun FlipAcceptListResponseDto.toFlipAcceptListResponse(): List<FlipAcceptListResponse> =
    this.data?.map { flip ->
        FlipAcceptListResponse(bankCode = flip.bankCode, flipBankCode = flip.flipBankCode,
            bankName = flip.bankName, imgUrl = flip.imgUrl, cost = flip.cost, priority = flip.priority
        )
    } ?: emptyList()

fun FlipAcceptRequest.toFlipAcceptRequestDto(): FlipAcceptRequestDto =
    FlipAcceptRequestDto(uuid = this.uuid, bankCode = this.bankCode, amount = this.amount)

fun CostFlipAcceptResponseDto.toCostFlipAcceptResponse(): CostFlipAcceptResponse =
    CostFlipAcceptResponse(amount = this.amount, cost = this.cost)

fun CreateBillFlipAcceptResponseDto.toFlipAcceptCreateBillResponse(): FlipAcceptCreateBillResponse =
    FlipAcceptCreateBillResponse(
        billLinkId = this.billLinkId, bankCode = this.bankCode, amount = this.amount, cost = this.cost,
        vaNumber = this.vaNumber, depositAmount = this.depositAmount
    )