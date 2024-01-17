package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter

data class HarborsResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @JsonAdapter(value = JsonArrayConverter::class)
    @field:SerializedName("data")
    val data: List<ItemHarbors>?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class ItemHarbors(
    @field:SerializedName("harbor_code")
    val harborCode: String,

    @field:SerializedName("harbor_name")
    val harborName: String
)
