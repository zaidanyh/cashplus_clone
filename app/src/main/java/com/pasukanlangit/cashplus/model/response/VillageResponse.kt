package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class VillageResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<VillageItem>
)

data class VillageItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("district_id")
	val districtId: String
)
