package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OprNameResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<DataItem>
)

data class DataItem(

	@field:SerializedName("opr_name")
	val oprName: String
)
