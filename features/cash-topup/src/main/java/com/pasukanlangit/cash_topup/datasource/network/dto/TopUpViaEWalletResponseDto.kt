package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpViaEWalletResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("status_code")
    val statusCode: String,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("txid")
    val txId: String,
    @field:SerializedName("pay_no")
    val pay_number: String?
)
