package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class HarborResponse(

	@field:SerializedName("endOfData")
	val endOfData: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<HarborDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class HarborDataItem(

	@field:SerializedName("harbor_name")
	val harborName: String,

	@field:SerializedName("harbor_code")
	val harborCode: String
)
