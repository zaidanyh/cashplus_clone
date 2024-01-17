package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class CalculateUnitPriceRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("qty")
    val qty: String
)
