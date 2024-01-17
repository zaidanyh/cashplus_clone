package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownlineTrxCountResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("downline_trx_count")
    val downlineTrxCount: String
)