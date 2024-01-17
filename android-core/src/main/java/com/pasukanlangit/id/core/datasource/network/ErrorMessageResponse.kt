package com.pasukanlangit.id.core.datasource.network

import com.google.gson.annotations.SerializedName

data class ErrorMessageResponse(
	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
