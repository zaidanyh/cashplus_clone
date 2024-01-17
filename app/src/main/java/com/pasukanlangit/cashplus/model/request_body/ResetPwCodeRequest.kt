package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ResetPwCodeRequest(

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("uuid")
	val uuid: String
)
