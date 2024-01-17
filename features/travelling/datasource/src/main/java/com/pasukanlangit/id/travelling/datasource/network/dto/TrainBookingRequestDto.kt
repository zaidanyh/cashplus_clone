package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TrainBookingRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("session")
    val session: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String,

    @field:SerializedName("train_code")
    val trainCode: String,

    @field:SerializedName("train_class")
    val trainClass: String,

    @field:SerializedName("train_subclass")
    val trainSubclass: String,

    @field:SerializedName("passengername")
    val passengerName: String,

    @field:SerializedName("idnumber")
    val idNumber: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("email")
    val email: String
)
