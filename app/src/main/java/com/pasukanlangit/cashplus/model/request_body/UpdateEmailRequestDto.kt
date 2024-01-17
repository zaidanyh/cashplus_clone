package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class UpdateEmailRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("email")
    val email: String
)
