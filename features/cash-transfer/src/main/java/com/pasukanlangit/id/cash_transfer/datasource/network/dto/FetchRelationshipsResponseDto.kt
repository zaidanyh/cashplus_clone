package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchRelationshipsResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<Relationships>?
)

data class Relationships(
    @field:SerializedName("code")
    val codeRelation: String,
    @field:SerializedName("dsc")
    val desc: String
)
