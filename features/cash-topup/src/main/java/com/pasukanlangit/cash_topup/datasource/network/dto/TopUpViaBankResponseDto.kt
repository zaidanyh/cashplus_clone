package com.pasukanlangit.cash_topup.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpViaBankResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("phone")
    val phone: String,
    @field:SerializedName("dest")
    val destination: String,
    @field:SerializedName("amount")
    val amount: String,
    @field:SerializedName("unique_id")
    val uniqueId: String,
    @field:SerializedName("bank")
    val bank_name: String,
    @field:SerializedName("bank_acc_num")
    val bankAccNumber: String,
    @field:SerializedName("bank_acc_name")
    val bankAccName: String
)
