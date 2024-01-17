package com.pasukanlangit.cashplus.model.request_body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionRequest (
    @field:SerializedName("uuid")
    val uuid: String,
    @field:SerializedName("kode_produk")
    val kode_produk: String,
    @field:SerializedName("dest")
    val dest: String,
    @field:SerializedName("pin")
    val pin: String,
    @field:SerializedName("qty")
    val qty: String? = null,
    @field:SerializedName("reqId")
    val reqId: String ?= null
) : Parcelable


