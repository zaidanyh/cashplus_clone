package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class HarborBookResponse(

	@field:SerializedName("endOfData")
	val endOfData: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: HarborBookData,

	@field:SerializedName("message")
	val message: String
)

data class HarborBookData(

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("paymentcode")
	val paymentcode: String
)
