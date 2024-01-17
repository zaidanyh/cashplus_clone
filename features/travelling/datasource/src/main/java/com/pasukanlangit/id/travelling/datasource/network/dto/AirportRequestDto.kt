package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class AirportRequestDto(
    @field:SerializedName("code")
    val code: String,

    @field:SerializedName("city")
    val city: String,

    @field:SerializedName("grup")
    val group: String,

    @field:SerializedName("uuid")
    val uuid: String
)
