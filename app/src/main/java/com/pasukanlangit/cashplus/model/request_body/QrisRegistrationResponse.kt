package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class QrisRegistrationResponse(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("qrUrl")
    val qrUrl: String?,

    @field:SerializedName("qrContent")
    val qrContent: String?
)
