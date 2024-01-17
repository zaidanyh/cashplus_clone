package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OTPResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val msg: String,
    @field:SerializedName("otp_link")
    val otpLink: String?
)
