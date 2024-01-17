package com.pasukanlangit.id.core.model

import com.google.gson.annotations.SerializedName

data class BalanceResponseCore(
    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("rc")
    val rc: String? = null,

    @field:SerializedName("balance")
    val balance: Double? = null
)
