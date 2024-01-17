package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class OprNameRequest(

	@field:SerializedName("kode_provider")
	val codeProvider: String,

	@field:SerializedName("uuid")
	val uuid: String
)
