package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ProvinceResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val province: List<ProvinceItem>
)

data class ProvinceItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
