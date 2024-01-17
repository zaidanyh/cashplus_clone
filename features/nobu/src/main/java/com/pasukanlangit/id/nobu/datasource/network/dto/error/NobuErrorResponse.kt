package com.pasukanlangit.id.nobu.datasource.network.dto.error

import com.google.gson.annotations.SerializedName

data class NobuErrorResponse(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("is_binded")
    val is_binded: String? = null,

    @field:SerializedName("originalExternalId")
    val originalExtId: String?,

    @field:SerializedName("originalPartnerReferenceNo")
    val originalPartnerRefNum: String?,

    @field:SerializedName("transactionDate")
    val transactionDate: String?
)
