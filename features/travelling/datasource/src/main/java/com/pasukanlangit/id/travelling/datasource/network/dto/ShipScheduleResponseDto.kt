package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ShipScheduleResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("data")
    val data: ShipScheduleData?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class ShipScheduleData(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("schedule")
    val schedule: List<ItemShipSchedule>?
)

data class ItemShipSchedule(
    @field:SerializedName("ship_name")
    val shipName: String,

    @field:SerializedName("ship_number")
    val shipNumber: String,

    @field:SerializedName("ship_code")
    val shipCode: String,

    @field:SerializedName("ship_from")
    val shipFrom: String,

    @field:SerializedName("ship_to")
    val shipTo: String,

    @field:SerializedName("ship_route")
    val shipRoute: String,

    @field:SerializedName("ship_date")
    val shipDate: String,

    @field:SerializedName("ship_datetime")
    val shipDatetime: String,

    @field:SerializedName("ship_infodatetime")
    val shipInfodatetime: String,

    @field:SerializedName("ship_inforoute")
    val shipInforoute: String,

    @field:SerializedName("ship_class")
    val shipClass: String,

    @field:SerializedName("ship_basicfare")
    val shipBasicfare: Int,

    @field:SerializedName("ship_admin")
    val shipAdmin: Int,

    @field:SerializedName("ship_price")
    val shipPrice: Int,

    @field:SerializedName("ship_maleseat")
    val shipMaleSeat: String,

    @field:SerializedName("ship_femaleseat")
    val shipFemaleSeat: String
)
