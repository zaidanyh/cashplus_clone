package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchCountryResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<Countries>?
)

data class Countries(
    @field:SerializedName("country_code")
    val codeCountry: String,
    @field:SerializedName("country_name")
    val nameCountry: String,
    @field:SerializedName("currency")
    val currency: String,
    @field:SerializedName("currency_dsc")
    val currencyDesc: String,
    @field:SerializedName("img_url")
    val imgUrl: String?
)
