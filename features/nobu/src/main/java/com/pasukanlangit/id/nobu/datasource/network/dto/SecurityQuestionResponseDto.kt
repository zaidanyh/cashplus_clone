package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class SecurityQuestionResponseDto(

    @field:SerializedName("responseCode")
    val responseCode: String,

    @field:SerializedName("responseMessage")
    val responseMessage: String,

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("data")
    val data: List<Questions>
)

data class Questions(
    @field:SerializedName("securityCode")
    val securityCode: String,

    @field:SerializedName("securityQuestion")
    val securityQuestion: String
)
