package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ChangePWRequest(

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("pass")
	val pass: String,

	val uuid: String
)
