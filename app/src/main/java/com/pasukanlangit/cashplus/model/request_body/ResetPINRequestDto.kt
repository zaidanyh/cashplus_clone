package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ResetPINRequestDto(
	@field:SerializedName("uuid")
	val uuid: String,
	@field:SerializedName("pin")
	val pin: String,
	@field:SerializedName("via")
	var via: String
)
