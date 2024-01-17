package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class AccountCreationResponseDto(
    @field:SerializedName("responseCode")
    val responseCode: String,

    @field:SerializedName("responseMessage")
    val responseMessage: String,

    @field:SerializedName("referenceNo")
    val referenceNo: String,

    @field:SerializedName("partnerReferenceNo")
    val partnerReferenceNp: String,

    @field:SerializedName("authCode")
    val authCode: String,

    @field:SerializedName("apiKey")
    val apiKey: String,

    @field:SerializedName("accountId")
    val accountId: String,

    @field:SerializedName("state")
    val state: String,

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val msg: String,

    @field:SerializedName("additionalInfo")
    val additional: AdditionalInfo
)

data class AdditionalInfo(
    @field:SerializedName("refferalCode")
    val referralCode: String,

    @field:SerializedName("securityAnswer")
    val securityAnswer: String,

    @field:SerializedName("securityQuestion")
    val securityQuestion: String,

    @field:SerializedName("securityCode")
    val securityCode: String
)
