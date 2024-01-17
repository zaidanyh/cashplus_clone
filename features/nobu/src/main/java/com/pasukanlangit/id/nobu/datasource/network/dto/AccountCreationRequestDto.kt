package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class AccountCreationRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("phoneNo")
    val phone: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("securityAnswer")
    val securityAnswer: String? = null,

    @field:SerializedName("securityQuestion")
    val securityQuestion: String? = null,

    @field:SerializedName("securityCode")
    val securityCode: String? = null,

    @field:SerializedName("redirectUrl")
    val redirectUrl: String? = null
)
