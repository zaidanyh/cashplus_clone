package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DetailMarkupPlanResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<DetailMarkups>?
)

data class DetailMarkups(
    @field:SerializedName("kode_markup_plan")
    val markupPlanCode: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("markup")
    val markup: String
)
