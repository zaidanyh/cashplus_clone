package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class DeleteBankRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("bank_code")
    val bank_code: String,

    @field:SerializedName("bank_account_number")
    val bank_account_num: String
)
