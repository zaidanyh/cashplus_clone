package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TrainPriceResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("data")
    val data: ItemTrainPrice?,

    @field:SerializedName("endOfData")
    val endOfData: String?
)

data class ItemTrainPrice(
    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("duration")
    val duration: Double,

    @field:SerializedName("session")
    val session: String,

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

    @field:SerializedName("train_datetime")
    val trainDatetime: String,

    @field:SerializedName("train_basicfare")
    val trainBasicfare: Int,

    @field:SerializedName("train_servicecharge")
    val trainServicecharge: Int,

    @field:SerializedName("train_totalfare")
    val trainTotalfare: Int,

    @field:SerializedName("train_class")
    val trainClass: String,

    @field:SerializedName("train_subclass")
    val trainSubClass: String,

    @field:SerializedName("train_available")
    val trainAvailable: String,

    @field:SerializedName("adult")
    val adult: String,

    @field:SerializedName("infant")
    val infant: String
)
