package com.pasukanlangit.id.rebate.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RemainingRebateResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("sum_rebate")
    val sum_rebate: String?
)