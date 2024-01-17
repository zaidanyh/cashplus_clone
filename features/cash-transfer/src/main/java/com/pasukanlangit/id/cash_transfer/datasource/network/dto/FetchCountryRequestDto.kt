package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class FetchCountryRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("country_name")
    val countryName: String?,
    @field:SerializedName("currency_dsc")
    val currencyDesc: String?
)
