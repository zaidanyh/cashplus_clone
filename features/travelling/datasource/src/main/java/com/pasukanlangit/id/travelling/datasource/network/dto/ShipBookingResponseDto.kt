package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ShipBookingResponseDto(
    @field:SerializedName("endOfData")
    val endOfData: String,

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("data")
    val data: ShipBookingData,

    @field:SerializedName("message")
    val message: String
)

data class ShipBookingData(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("paymentcode")
    val paymentCode: String
)
