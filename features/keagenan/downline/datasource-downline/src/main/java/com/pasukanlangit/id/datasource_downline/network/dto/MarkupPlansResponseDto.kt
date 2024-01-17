package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class MarkupPlansResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val markupPlans: List<MarkupPlans>?
)

data class MarkupPlans(
    @field:SerializedName("kode_markup_plan")
    val markupPlanCode: String,
    @field:SerializedName("dsc")
    val description: String
)
