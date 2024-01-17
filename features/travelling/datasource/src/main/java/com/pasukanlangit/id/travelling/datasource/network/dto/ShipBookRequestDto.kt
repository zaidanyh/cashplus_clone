package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ShipBookRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("from")
    val from: String,

    @field:SerializedName("to")
    val to: String,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("ship_name")
    val shipName: String,

    @field:SerializedName("ship_number")
    val shipNumber: String,

    @field:SerializedName("ship_code")
    val shipCode: String,

    @field:SerializedName("ship_class")
    val shipClass: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String,

    @field:SerializedName("passengername")
    val passengername: String,

    @field:SerializedName("dateofbirth")
    val dateofbirth: String,

    @field:SerializedName("idnumber")
    val idnumber: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("email")
    val email: String
)
