package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class BookingResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("data")
    val data: BookingData?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class BookingData(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("kodebooking")
    val bookingCode: String?,

    @field:SerializedName("paymentcode")
    val paymentCode: String?
)
