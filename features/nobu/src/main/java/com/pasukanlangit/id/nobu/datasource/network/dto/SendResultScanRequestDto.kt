package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class SendResultScanRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("qrData")
    val resultScan: String,

    @field:SerializedName("partnerRedirectUrl")
    val callBackUrl: String
)
