package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ResetPinOtpCodeRequest(

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("uuid")
	val uuid: String
)
