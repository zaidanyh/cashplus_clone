package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class IsBindedResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("is_binded")
    val isBinded: String
)
