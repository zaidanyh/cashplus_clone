package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class LocalBankListResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val msg: String,
    @field:SerializedName("data")
    val data: List<Bank>?
)

data class Bank(
    @field:SerializedName("bank_code")
    val bank_code: String,
    @field:SerializedName("bank_name")
    val bank_name: String,
    @field:SerializedName("perbifast_code")
    val biFastCode: String,
    @field:SerializedName("img_url")
    val imgBank: String
)
