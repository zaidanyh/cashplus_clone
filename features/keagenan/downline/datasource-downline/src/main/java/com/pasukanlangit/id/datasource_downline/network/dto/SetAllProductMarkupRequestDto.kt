package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName


data class SetAllProductMarkupRequestDto(

    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("dest")
    val dest: String,

    @field:SerializedName("markup")
    val newMarkup: String,

    @field:SerializedName("kode_produk")
    val productCode: String = "all"
)