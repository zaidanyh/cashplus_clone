package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ProductStoreRequest(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("row_start")
	val rowStart: String? = null,

	@field:SerializedName("row_end")
	val rowEnd: String? = null
)
