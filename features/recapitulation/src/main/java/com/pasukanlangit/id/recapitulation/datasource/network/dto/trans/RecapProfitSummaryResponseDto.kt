package com.pasukanlangit.id.recapitulation.datasource.network.dto.trans

import com.google.gson.annotations.SerializedName

data class RecapProfitSummaryResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<TransactionProfit>?
)

data class TransactionProfit(
    @field:SerializedName("qty")
    val qty: String,
    @field:SerializedName("total_modal")
    val modal: String,
    @field:SerializedName("total_jual")
    val selling: String,
    @field:SerializedName("profit")
    val profit: String
)
