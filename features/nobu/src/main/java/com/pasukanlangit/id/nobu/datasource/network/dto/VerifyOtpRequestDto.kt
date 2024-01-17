package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("otp")
    val otp: String
)
