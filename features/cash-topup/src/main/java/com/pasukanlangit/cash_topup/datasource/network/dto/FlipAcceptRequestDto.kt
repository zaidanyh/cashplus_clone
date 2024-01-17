package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FlipAcceptRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("bank_code")
    val bankCode: String,
    @field:SerializedName("amt")
    val amount: String
)
