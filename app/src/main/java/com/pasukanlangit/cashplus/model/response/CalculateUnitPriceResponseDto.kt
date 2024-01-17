package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class CalculateUnitPriceResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val msg: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("qty")
    val qty: String,
    @field:SerializedName("unit_price")
    val unitPrice: String,
    @field:SerializedName("markup")
    val markup: String,
    @field:SerializedName("price")
    val price: String,
    @field:SerializedName("admin")
    val admin: String,
    @field:SerializedName("total_price")
    val totalPrice: String
)
