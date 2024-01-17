package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchNationResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<Nation>?
)

data class Nation(
    @field:SerializedName("nation_code")
    val nationCode: String,
    @field:SerializedName("nation_name")
    val nationName: String
)
