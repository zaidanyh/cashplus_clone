package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownLineSummaryRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("date_start")
    val dateStart: String,

    @field:SerializedName("date_end")
    val dateEnd: String
)
