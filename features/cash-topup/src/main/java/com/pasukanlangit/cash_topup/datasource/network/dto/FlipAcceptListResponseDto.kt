package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FlipAcceptListResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<FlipAcceptResponse>?
)

data class FlipAcceptResponse(
    @field:SerializedName("bank_code")
    val bankCode: String,
    @field:SerializedName("flip_bank_code")
    val flipBankCode: String,
    @field:SerializedName("bank_name")
    val bankName: String,
    @field:SerializedName("img_url")
    val imgUrl: String?,
    @field:SerializedName("biaya")
    val cost: String,
    @field:SerializedName("priority")
    val priority: String
)
