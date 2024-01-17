package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class GlobalBankCreateTransRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("BxQuoteId")
    val bxQuoteId: String,
    @field:SerializedName("Relationship")
    val relationshipCode: String,
    @field:SerializedName("bank_code")
    val bankCode: String,
    @field:SerializedName("acc_num")
    val bankAccNum: String,
    @field:SerializedName("country_code")
    val countryCode: String,
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("addr_street")
    val addressStreet: String,
    @field:SerializedName("addr_city")
    val addressCity: String,
    @field:SerializedName("nation_code")
    val nationCode: String
)
