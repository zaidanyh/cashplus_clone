package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class LocalBankSavedResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val message: String,

    @field:SerializedName("data")
    val listData: List<DataBank>?
)

data class DataBank(
    @field:SerializedName("bank_code")
    val bank_code: String,

    @field:SerializedName("bank_name")
    val bank_name: String,

    @field:SerializedName("bank_account_number")
    val bank_account_number: String,

    @field:SerializedName("bank_account_name")
    val bank_account_name: String,

    @field:SerializedName("img_url")
    val img_url: String
)
