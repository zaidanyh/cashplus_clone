package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TopUpQrisRequest(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("value")
    val valueTopUp: String,

    @SerializedName("pin")
    val pin: String
)