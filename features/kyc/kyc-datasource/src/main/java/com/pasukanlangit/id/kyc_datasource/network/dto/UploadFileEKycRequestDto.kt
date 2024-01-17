package com.pasukanlangit.id.kyc_datasource.network.dto

import com.google.gson.annotations.SerializedName

data class UploadFileEKycRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("upload_type")
    val uploadType: String,

    @field:SerializedName("base64_data")
    val base64Data: String
)
