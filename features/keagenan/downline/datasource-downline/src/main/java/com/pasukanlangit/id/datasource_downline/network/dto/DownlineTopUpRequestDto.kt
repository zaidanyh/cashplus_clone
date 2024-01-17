package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownlineTopUpRequestDto(

	@field:SerializedName("pin")
	val pin: String,

	@field:SerializedName("dest")
	val dest: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("value")
	val value: String,

	@field:SerializedName("payment_method")
	val paymentMethod: String
)
