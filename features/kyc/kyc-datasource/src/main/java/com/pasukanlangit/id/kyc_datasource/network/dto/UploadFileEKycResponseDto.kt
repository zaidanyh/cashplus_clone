package com.pasukanlangit.id.kyc_datasource.network.dto

import com.google.gson.annotations.SerializedName

data class UploadFileEKycResponseDto(

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("upload_type")
    val uploadType: String,

    @field:SerializedName("id_type")
    val idType: String?,

    @field:SerializedName("file_ext")
    val fileExt: String?,

    @field:SerializedName("msg")
    val message: String
)