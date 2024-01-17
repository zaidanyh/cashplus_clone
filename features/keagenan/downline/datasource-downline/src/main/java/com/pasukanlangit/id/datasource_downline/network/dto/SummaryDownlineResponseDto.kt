package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class SummaryDownlineResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("full_name")
    val fullname: String,

    @field:SerializedName("my_balance")
    val myBalance: String?,

    @field:SerializedName("my_trx_count")
    val myTrxCount: String,

    @field:SerializedName("downline_count")
    val downlineCount: String,

    @field:SerializedName("downline_total_balance")
    val downlineTotalBalance: String?
)
