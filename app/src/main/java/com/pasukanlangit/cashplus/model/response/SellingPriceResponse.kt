package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class SellingPriceResponse(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("outlet_price")
    val outletPrice: String
)
