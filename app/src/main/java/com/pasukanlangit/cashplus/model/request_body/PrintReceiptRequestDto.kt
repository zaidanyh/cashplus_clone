package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class PrintReceiptRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("trx_id")
    val trxId: String,
    @field:SerializedName("adjust_price")
    val adjustPrice: String? = null
)
