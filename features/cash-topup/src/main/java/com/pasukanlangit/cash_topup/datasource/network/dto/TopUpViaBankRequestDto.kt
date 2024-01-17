package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpViaBankRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("bank")
    val bank_name: String,
    @field:SerializedName("value")
    val value: String
)
