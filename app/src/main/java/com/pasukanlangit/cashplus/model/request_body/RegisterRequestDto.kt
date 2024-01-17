package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto (
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("phone")
    val phone : String,
    @field:SerializedName("full_name")
    var full_name : String,
    @field:SerializedName("email")
    var email : String,
    @field:SerializedName("referral")
    val referral: String?,
    @field:SerializedName("owner_name")
    var owner_name: String?,
    @field:SerializedName("via")
    var via: String
)