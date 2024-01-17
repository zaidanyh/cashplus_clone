package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class BalanceInquiryResponse(
    @field:SerializedName("responseCode")
    val responseCode: String,

    @field:SerializedName("responseMessage")
    val responseMessage: String,

    @field:SerializedName("referenceNo")
    val referenceNo: String?,

    @field:SerializedName("partnerReferenceNo")
    val partnerReferenceNo: String,

    @field:SerializedName("accountNo")
    val accountNo: String?,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("accountInfos")
    val accountInfos: List<AccountInfosItem>,

    @field:SerializedName("additionalInfo")
    val additionalInfo: AdditionalInfoBalance?,

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String
)

data class AccountInfosItem(

    @field:SerializedName("balanceType")
    val balanceType: String,

    @field:SerializedName("amount")
    val amount: BalanceAmount,

    @field:SerializedName("floatAmount")
    val floatAmount: BalanceAmount,

    @field:SerializedName("holdAmount")
    val holdAmount: BalanceAmount,

    @field:SerializedName("availableBalance")
    val availableBalance: BalanceAmount,

    @field:SerializedName("ledgerBalance")
    val ledgerBalance: BalanceAmount,

    @field:SerializedName("currentMultilateralLimit")
    val currentMultilateralLimit: BalanceAmount,

    @field:SerializedName("registrationStatusCode")
    val registrationStatusCode: String,

    @field:SerializedName("status")
    val status: String
)

data class BalanceAmount(
    @field:SerializedName("currency")
    val currency: String,

    @field:SerializedName("value")
    val value: String
)

data class AdditionalInfoBalance(
    val any: Any? = null
)
