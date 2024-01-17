package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TransferTransactionRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("dest")
    val dest: String,
    @field:SerializedName("pin")
    val pin: String,
    @field:SerializedName("reqId")
    val reqId: String?
)
