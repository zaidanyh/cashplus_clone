package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class UpdateFirebaseTokenRequest(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("token")
	val token: String
)
