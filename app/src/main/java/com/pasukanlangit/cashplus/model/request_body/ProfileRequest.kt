package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @field:SerializedName("uuid")
    val uuid: String
)