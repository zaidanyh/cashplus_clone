package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TopUpBankRequest(

	@field:SerializedName("bank")
	val bank: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("value")
	val value: String
)
