package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class NicePayEwalletResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("status_code")
	val statusCode: String,

	@field:SerializedName("txid")
	val txid: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("pay_no")
	val payNo: String?
)
