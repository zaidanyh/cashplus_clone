package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class CreateBillFlipAcceptResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("bill_link_id")
    val billLinkId: String,
    @field:SerializedName("bank_code")
    val bankCode: String,
    @field:SerializedName("amt")
    val amount: String,
    @field:SerializedName("biaya")
    val cost: String,
    @field:SerializedName("va_number")
    val vaNumber: String,
    @field:SerializedName("deposit_amt")
    val depositAmount: String
)
