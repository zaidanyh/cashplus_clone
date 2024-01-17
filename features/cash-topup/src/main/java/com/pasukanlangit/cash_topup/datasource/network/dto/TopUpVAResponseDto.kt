package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpVAResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("status_code")
    val statusCode: String,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("va_number")
    val vaNumber: String
)
