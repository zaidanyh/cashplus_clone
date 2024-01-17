package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class StationsRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("code")
    val code: String,

    @field:SerializedName("location")
    val location: String
)
