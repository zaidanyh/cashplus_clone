package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class StationsRequest(

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("location")
	val location: String,

	@field:SerializedName("uuid")
	val uuid: String
)
