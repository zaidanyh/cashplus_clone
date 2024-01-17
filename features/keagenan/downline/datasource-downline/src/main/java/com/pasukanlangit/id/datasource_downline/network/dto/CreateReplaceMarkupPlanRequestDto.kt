package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class CreateReplaceMarkupPlanRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_markup_plan")
    val markupCode: String,
    @field:SerializedName("dsc")
    val description: String,
    @field:SerializedName("data")
    val products: List<ProductMarkupPlan>
)

data class ProductMarkupPlan(
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("markup")
    val markup: String
)
