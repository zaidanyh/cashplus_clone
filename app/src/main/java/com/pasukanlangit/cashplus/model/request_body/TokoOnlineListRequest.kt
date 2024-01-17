package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TokoOnlineListRequest(

	@field:SerializedName("category_name")
	val categoryName: String?,

	@field:SerializedName("row_start")
	val rowStart: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("row_end")
	val rowEnd: String
)
