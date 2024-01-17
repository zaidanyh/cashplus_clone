package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OmniPackageOrderResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("payment_code")
    val paymentCode: String
)
