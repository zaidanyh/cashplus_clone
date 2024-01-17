package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ShipScheduleRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String,

    @field:SerializedName("male")
    val male: String,

    @field:SerializedName("female")
    val female: String
)
