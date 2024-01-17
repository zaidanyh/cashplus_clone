package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class SubdistrictShipmentRequest(

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("city_id")
	val cityId: String? = null
)
