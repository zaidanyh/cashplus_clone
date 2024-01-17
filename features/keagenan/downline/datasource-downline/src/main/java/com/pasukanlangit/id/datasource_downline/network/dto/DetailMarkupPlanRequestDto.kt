package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DetailMarkupPlanRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_markup_plan")
    val markupPlanCode: String,
    @field:SerializedName("downline_phone")
    val downlinePhone: String? = null
)
