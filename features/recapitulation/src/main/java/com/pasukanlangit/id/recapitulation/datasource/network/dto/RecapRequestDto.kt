package com.pasukanlangit.id.recapitulation.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RecapRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("date_start")
    val dateStart: String,
    @field:SerializedName("date_end")
    val dateEnd: String,
    @field:SerializedName("row_start")
    val rowStart: String,
    @field:SerializedName("is_summary")
    val isSummary: String
)
