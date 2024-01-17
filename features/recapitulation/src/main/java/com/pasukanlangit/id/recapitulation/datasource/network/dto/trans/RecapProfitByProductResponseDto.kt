package com.pasukanlangit.id.recapitulation.datasource.network.dto.trans

import com.google.gson.annotations.SerializedName

data class RecapProfitByProductResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<ProfitByProduct>?
)

data class ProfitByProduct(
    @field: SerializedName("nom")
    val productCode: String,
    @field:SerializedName("qty")
    val qty: String,
    @field:SerializedName("total_modal")
    val modal: String,
    @field:SerializedName("total_jual")
    val selling: String,
    @field:SerializedName("profit")
    val profit: String,
    @field:SerializedName("dsc")
    val description: String,
    @field:SerializedName("short_dsc")
    val shortDesc: String,
    @field:SerializedName("opr_name")
    val operatorName: String,
    @field:SerializedName("category")
    val category: String,
    @field:SerializedName("kode_provider")
    val providerCode: String
)
