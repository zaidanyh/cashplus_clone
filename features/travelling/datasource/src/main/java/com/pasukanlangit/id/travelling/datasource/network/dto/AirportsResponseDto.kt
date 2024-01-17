package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.JsonArrayConverter

data class AirportsResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String,

    @JsonAdapter(value = JsonArrayConverter::class)
    @field:SerializedName("data")
    val data: List<AirPortDataItem>?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class AirPortDataItem(

    @field:SerializedName("code")
    val code: String,

    @field:SerializedName("city")
    val city: String,

    @field:SerializedName("grup")
    val group: String,

    @field:SerializedName("airport")
    val airport: String,

    @field:SerializedName("status")
    val status: String
)
