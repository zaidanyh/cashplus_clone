package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ProductRequest (
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("kode_provider")
    val kode_provider : String,

    @field:SerializedName("is_faktur")
    val is_faktur : String,

    @field:SerializedName("is_active")
    val is_active : String,

    @field:SerializedName("is_bulk")
    val is_bulk: String = "0",

    @field:SerializedName("opr_name")
    val opr_name : String ?= null,

    @field:SerializedName("prefix")
    val prefix: String? = null
)