package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OmniMenuResponseDto(
    @field:SerializedName("rc")
    val rc: String?,
    @field:SerializedName("msisdn")
    val msidn: String?,
    @field:SerializedName("menu")
    val menu: List<OmniMenusResponse>?,
    @field:SerializedName("error")
    val error: String? = null
)

data class OmniMenusResponse(
    @field:SerializedName("icon")
    val icon: String,
    @field:SerializedName("mlid")
    val mlid: String,
    @field:SerializedName("title")
    val title: OmniMenuTitle
)

data class OmniMenuTitle(
    @field:SerializedName("en")
    val titleEN: String,
    @field:SerializedName("id")
    val titleID: String
)
