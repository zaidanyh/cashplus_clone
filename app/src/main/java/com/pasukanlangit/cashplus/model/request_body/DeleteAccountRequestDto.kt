package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class DeleteAccountRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("unsub_reason")
    val reason: String
)
