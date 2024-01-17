package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ResetPassRequestDto(
	@field:SerializedName("uuid")
	val uuid: String,
	@field:SerializedName("phone")
	val phone: String,
	@field:SerializedName("pass")
	val newPass: String,
	@field:SerializedName("via")
	var via: String
)
