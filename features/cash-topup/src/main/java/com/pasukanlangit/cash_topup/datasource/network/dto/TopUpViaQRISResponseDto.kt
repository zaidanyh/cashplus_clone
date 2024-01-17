package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpViaQRISResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("qrUrl")
    val qrUrl: String?,
    @field:SerializedName("qrContent")
    val qrContent: String?
)
