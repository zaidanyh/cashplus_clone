package com.pasukanlangit.id.recapitulation.datasource.network.dto.deposit

import com.google.gson.annotations.SerializedName

data class RecapDepositMutationResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val message: String,
    @field:SerializedName("data")
    val data: List<DepositMutation>?
)

data class DepositMutation(
    @field:SerializedName("row_num")
    val rowNum: String,
    @field:SerializedName("trx_date")
    val dateTrans: String,
    @field:SerializedName("value")
    val value: String,
    @field:SerializedName("db_cr")
    val isDBorCR: String,
    @field:SerializedName("ending_balance")
    val endingBalance: String,
    @field:SerializedName("trans_stat")
    val transStat: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("no_hp")
    val destination: String,
    @field:SerializedName("dsc")
    val description: String,
    @field:SerializedName("trans_stat_dsc")
    val transStatDsc: String,
    @field:SerializedName("produk_dsc")
    val productDesc: String,
    @field:SerializedName("kode_provider")
    val providerCode: String,
    @field:SerializedName("category")
    val category: String
)
