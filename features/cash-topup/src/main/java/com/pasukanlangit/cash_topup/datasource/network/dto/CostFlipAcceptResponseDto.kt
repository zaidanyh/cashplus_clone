package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class CostFlipAcceptResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("amt")
    val amount: String,
    @field:SerializedName("biaya")
    val cost: String
)
