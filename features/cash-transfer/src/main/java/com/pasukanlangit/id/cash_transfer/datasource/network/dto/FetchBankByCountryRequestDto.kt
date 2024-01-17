package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchBankByCountryRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("country_code")
    val codeCountry: String
)
