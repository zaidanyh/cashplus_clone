package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TransactionResponseDto(

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("dest")
    val destination: String,

    @field:SerializedName("rc")
    val rc: String? = null,

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("rc_msg")
    val rc_msg : String ?= null,

    @field:SerializedName("trx_id")
    val trxId: String,

    @field:SerializedName("kode_produk")
    val productCode: String? = null,

    @field:SerializedName("dsc")
    val dsc: String? = null,

    @field:SerializedName("short_dsc")
    val shortDsc: String,

    @field:SerializedName("fee")
    val fee: String?,

    @field:SerializedName("price")
    val price: String?,

    @field:SerializedName("bill_data")
    val billData: BillData? = null,
)

data class BillData(
    @field:SerializedName("nama")
    val name: String,

    @field:SerializedName("tagihan")
    val bill: String?,

    @field:SerializedName("admin")
    val adminCost: String?,

    @field:SerializedName("info")
    val info: String,

    @field:SerializedName("total")
    val total: String
)
