package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(

	@field:SerializedName("new_pass")
	val newPass: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("old_pass")
	val oldPass: String
)
