package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TransactionCheckRequest(

	@field:SerializedName("uuid")
	var uuid: String,

	@field:SerializedName("dest")
	var dest: String,

	@field:SerializedName("limit")
	val limit: String? = null
)
