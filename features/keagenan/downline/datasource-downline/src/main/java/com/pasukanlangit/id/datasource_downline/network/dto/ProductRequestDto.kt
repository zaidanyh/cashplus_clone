package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class ProductRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("keyword")
    val keyword: String = "",
    @field:SerializedName("category")
    val category: String = "",
    @field:SerializedName("kode_provider")
    val providerCode: String = "",
    @field:SerializedName("opr_name")
    val opr_name: String = "",
    @field:SerializedName("is_faktur")
    val is_faktur: String = "",
    @field:SerializedName("is_active")
    val is_active: String = "",
    @field:SerializedName("is_non_markup")
    val is_non_markup: String = "0",
    @field:SerializedName("page_size")
    val page_size: String = "",
    @field:SerializedName("page_num")
    val page_num: String = ""
)
