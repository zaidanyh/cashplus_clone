package com.pasukanlangit.id.kyc_datasource.network.dto

import com.google.gson.annotations.SerializedName

data class VerifyEKycResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("ocr_nik")
    val ocrNik: String?,

    @field:SerializedName("ocr_full_name")
    val ocrFullName: String?,

    @field:SerializedName("nik")
    val nik: String?,

    @field:SerializedName("full_name")
    val fullName: String?,

    @field:SerializedName("owner_name")
    val ownerName: String?
)
