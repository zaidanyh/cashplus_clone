package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class NicepayRegistrationResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("status_code")
	val statusCode: String,

	@field:SerializedName("va_number")
	val vaNumber: String,

	@field:SerializedName("message")
	val message: String
)
