package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter

data class StationsResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @JsonAdapter(value = JsonArrayConverter::class)
    @field:SerializedName("data")
    val data: List<ItemStation>?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class ItemStation(
    @field:SerializedName("code")
    val code: String,

    @field:SerializedName("location")
    val location: String
)
