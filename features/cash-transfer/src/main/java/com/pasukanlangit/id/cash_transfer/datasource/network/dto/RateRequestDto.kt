package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RateRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("currency")
    val currency: String,
    @field:SerializedName("amount")
    val amount: String
)
