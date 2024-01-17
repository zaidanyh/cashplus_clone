package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RateResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("BxQuoteId")
    val quoteId: String,
    @field:SerializedName("SendingCcy")
    val sendCurrency: String,
    @field:SerializedName("ReceivingCcy")
    val receiveCurrency: String,
    @field:SerializedName("SendingAmount")
    val sendAmount: String,
    @field:SerializedName("ReceivingAmount")
    val receiveAmount: String,
    @field:SerializedName("Rate")
    val rate: Double,
    @field:SerializedName("QuoteExpDateTime")
    val expDateTime: String,
    @field:SerializedName("QuoteExpInSec")
    val expInSec: Int,
    @field:SerializedName("Fee")
    val fee: Double,
    @field:SerializedName("ClientMarkupRate")
    val markupRate: Double? = null,
    @field:SerializedName("usd_idr_rate")
    val usdIdrRate: String,
    @field:SerializedName("kurs_markup")
    val kursMarkup: String,
    @field:SerializedName("idr_fee_apps")
    val feeApps: String,
    @field:SerializedName("idr_admin_apps")
    val adminApps: String,
    @field:SerializedName("idr_admin_bridgepay")
    val adminBridgePay: String,
    @field:SerializedName("idr_admin_total")
    val adminTotal: String,
    @field:SerializedName("idr_amount_send")
    val idrAmountSend: String,
    @field:SerializedName("idr_amount")
    val idrAmount: String,
    @field:SerializedName("idr_amount_plus_fee_apps")
    val idrAmountPlusFeeApps: String
)
