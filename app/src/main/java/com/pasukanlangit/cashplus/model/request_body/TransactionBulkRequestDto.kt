package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TransactionBulkRequestDto(
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_produk")
    val productCode: String,
    @field:SerializedName("pin")
    val pin: String,
    @field:SerializedName("data")
    val dataBulk: List<DataBulk>
)

data class DataBulk(
    @field:SerializedName("dest")
    val destination: String,
    @field:SerializedName("reqid")
    val reqId: String
)
