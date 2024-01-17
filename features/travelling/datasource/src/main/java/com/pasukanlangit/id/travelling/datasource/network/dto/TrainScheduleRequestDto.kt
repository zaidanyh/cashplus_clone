package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TrainScheduleRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("date")
    var date: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String
)
