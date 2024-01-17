package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class SellingPriceRequest(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_produk")
    val productCode: String
)
