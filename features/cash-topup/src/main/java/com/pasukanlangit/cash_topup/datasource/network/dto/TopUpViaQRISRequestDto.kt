package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpViaQRISRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("amt")
    val amount: String
)
