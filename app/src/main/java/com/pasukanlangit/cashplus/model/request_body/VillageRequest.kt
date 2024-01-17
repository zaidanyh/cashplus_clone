package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class VillageRequest(

	@field:SerializedName("district_id")
	val districtId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
