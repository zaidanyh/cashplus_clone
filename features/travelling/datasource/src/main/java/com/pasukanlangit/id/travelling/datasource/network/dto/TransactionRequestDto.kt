package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TransactionRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("kode_produk")
    val codeProduct: String,

    @field:SerializedName("dest")
    val destination: String,

    @field:SerializedName("pin")
    val pin: String
)
