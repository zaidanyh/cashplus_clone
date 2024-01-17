package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("value")
    val value: String
)
