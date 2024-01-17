package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class LoginByOtpRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("phone")
    val phone: String,
    @field:SerializedName("via")
    val via: String
)
