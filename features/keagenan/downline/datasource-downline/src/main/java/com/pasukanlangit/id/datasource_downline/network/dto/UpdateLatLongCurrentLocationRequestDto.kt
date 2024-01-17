package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateLatLongCurrentLocationRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("lat")
    val lat: Double,
    @field:SerializedName("long")
    val long: Double
)
