package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class VirtualAccountTopUpRequest(

	@field:SerializedName("store_id")
	val storeId: String,

	@field:SerializedName("bank_code")
	val bankCode: String,

	@field:SerializedName("user_name")
	val userName: String,

	@field:SerializedName("customer_name")
	val customerName: String,

	@field:SerializedName("msisdn")
	val msisdn: String
)
