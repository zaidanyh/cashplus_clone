package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ChangePinRequest(
	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("old_pin")
	val oldPin: String,

	@field:SerializedName("new_pin")
	val newPin: String

)
