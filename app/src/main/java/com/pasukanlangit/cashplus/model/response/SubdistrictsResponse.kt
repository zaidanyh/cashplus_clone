package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class SubdistrictsResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<SubdistrictItem>
)

data class SubdistrictItem(

	@field:SerializedName("regency_id")
	val regencyId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
