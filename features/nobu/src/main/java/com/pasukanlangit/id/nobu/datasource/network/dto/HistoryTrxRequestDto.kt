package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class HistoryTrxRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("date_start")
    val dateStart: String,

    @field:SerializedName("date_end")
    val dateEnd: String,

    @field:SerializedName("pageNumber")
    val pageNumber: String,

    @field:SerializedName("pageSize")
    val pageSize: String
)
