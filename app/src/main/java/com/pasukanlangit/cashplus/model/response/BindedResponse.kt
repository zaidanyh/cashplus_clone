package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class BindedResponse(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("is_binded")
    val isBinded: String
)
