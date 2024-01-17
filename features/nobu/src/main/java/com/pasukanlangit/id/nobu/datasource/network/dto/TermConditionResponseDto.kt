package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TermConditionResponseDto(
    @field:SerializedName("responseCode")
    val responseCode: String,

    @field:SerializedName("responseMessage")
    val responseMessage: String,

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("data")
    val data: TermCondition
)

data class TermCondition(
    @field:SerializedName("url")
    val url: String,

    @field:SerializedName("html")
    val html: String
)
