package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class ProductSearchRequest(

	@field:SerializedName("row_start")
	val rowStart: String,

	@field:SerializedName("keyword")
	val keyword: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("row_end")
	val rowEnd: String
)
