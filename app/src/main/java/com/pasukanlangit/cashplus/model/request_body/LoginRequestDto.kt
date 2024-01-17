package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class LoginRequestDto (
	@field:SerializedName("uuid")
	val uuid: String,
	@field:SerializedName("phone")
	val phone: String
)