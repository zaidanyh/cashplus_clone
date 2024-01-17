package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class TrxRequestDto(

	@field:SerializedName("pin")
	val pin: String,

	@field:SerializedName("dest")
	val dest: String,

	@field:SerializedName("kode_produk")
	val kodeProduk: String,

	@field:SerializedName("uuid")
	val uuid: String
)
