package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class PricePlanRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("filter")
    val filter: String?
)