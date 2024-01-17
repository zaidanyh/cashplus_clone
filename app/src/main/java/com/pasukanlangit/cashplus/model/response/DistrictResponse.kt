package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class DistrictsResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<DistrictsItem>
)

data class DistrictsItem(

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
