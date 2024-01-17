package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class SavingGlobalBankRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("bank_code")
    val bank_code: String,
    @field:SerializedName("bank_account_number")
    val bank_account_number: String,
    @field:SerializedName("bank_account_name")
    val bank_account_name: String,
    @field:SerializedName("relationship")
    val relationshipCode: String,
    @field:SerializedName("nation_code")
    val nationCode: String,
    @field:SerializedName("addr_street")
    val addressStreet: String,
    @field:SerializedName("addr_city")
    val addressCity: String
)
