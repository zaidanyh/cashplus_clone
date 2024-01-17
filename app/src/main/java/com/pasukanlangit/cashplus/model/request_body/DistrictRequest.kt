package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class DistrictRequest(
	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
