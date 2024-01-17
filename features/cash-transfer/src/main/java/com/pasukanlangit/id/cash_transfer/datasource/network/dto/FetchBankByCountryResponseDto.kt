package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchBankByCountryResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<GlobalBanks>?
)

data class GlobalBanks(
    @field: SerializedName("bank_code")
    val codeBank: String,
    @field:SerializedName("country_code")
    val codeCountry: String,
    @field:SerializedName("swift_code")
    val codeSwift: String,
    @field:SerializedName("bank_name")
    val bankName: String,
    @field:SerializedName("img_url")
    val imgUrl: String? = ""
)
