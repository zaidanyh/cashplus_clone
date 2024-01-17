package com.pasukanlangit.id.kyc_datasource.network.dto

import com.google.gson.annotations.SerializedName

data class VerifyEKycRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("verification_type")
    val verificationType: String
)
