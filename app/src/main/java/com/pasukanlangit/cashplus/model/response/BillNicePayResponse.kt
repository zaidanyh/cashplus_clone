package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class BillNicePayResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("status_code")
	val statusCode: String,

	@field:SerializedName("biaya")
	val biaya: String,

	@field:SerializedName("message")
	val message: String
)
