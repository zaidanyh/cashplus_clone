package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class GlobalBankSavedResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val savedList: List<GlobalBankSaved>?
)

data class GlobalBankSaved(
    @field:SerializedName("bank_code")
    val codeBank: String,
    @field:SerializedName("country_code")
    val codeCountry: String,
    @field:SerializedName("country_name")
    val nameCountry: String,
    @field:SerializedName("relationship")
    val relationship: String,
    @field:SerializedName("nation_code")
    val nationCode: String,
    @field:SerializedName("addr_street")
    val addressStreet: String,
    @field:SerializedName("addr_city")
    val addressCity: String,
    @field:SerializedName("currency")
    val currency: String,
    @field:SerializedName("currency_dsc")
    val currencyDesc: String,
    @field:SerializedName("bank_name")
    val bankName: String,
    @field:SerializedName("bank_account_number")
    val accountNumber: String,
    @field:SerializedName("bank_account_name")
    val accountName: String,
    @field:SerializedName("img_url")
    val imgUrl: String?,
    @field:SerializedName("country_img_url")
    val countryImgUrl: String?
)
