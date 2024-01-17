package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TrainScheduleResponseDto(

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("data")
    val data: TrainSchedule?,

    @field:SerializedName("endOfData")
    val endOfData: String
)

data class TrainSchedule(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("duration")
    val duration: Double,

    @field:SerializedName("session")
    val session: String,

    @field:SerializedName("schedule")
    val schedule: List<ItemTrainSchedule>?
)

data class ItemTrainSchedule(
    @field:SerializedName("train_name")
    val trainName: String,

    @field:SerializedName("train_code")
    val trainCode: String,

    @field:SerializedName("train_id")
    val trainId: String,

    @field:SerializedName("train_from")
    val trainFrom: String,

    @field:SerializedName("train_to")
    val trainTo: String,

    @field:SerializedName("train_route")
    val trainRoute: String,

    @field:SerializedName("train_date")
    val trainDate: String,

    @field:SerializedName("train_departure")
    val trainDeparture: String,

    @field:SerializedName("train_arrival")
    val trainArrival: String,

    @field:SerializedName("train_datetime")
    val trainDatetime: String,

    @field:SerializedName("train_fare")
    val trainFare: Int,

    @field:SerializedName("train_class")
    val trainClass: String,

    @field:SerializedName("train_subclass")
    val trainSubclass: String,

    @field:SerializedName("train_available")
    val trainAvailable: String,
)