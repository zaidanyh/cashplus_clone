package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpNicePayRegistrationRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("bank_mitra_code")
    val bankMitraCode: String,
    @field:SerializedName("pay_method")
    val payMethodCode: String,
    @field:SerializedName("amt")
    val amount: String,
    @field:SerializedName("billing_phone")
    val billingPhone: String? = null
)
