package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class PrintReceiptResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val msg: String,
    @field:SerializedName("fee")
    val fee: String,
    @field:SerializedName("fee_loket")
    val feeCounter: String,
    @field:SerializedName("price")
    val price: String,
    @field:SerializedName("receipt")
    val receipt: String
)
