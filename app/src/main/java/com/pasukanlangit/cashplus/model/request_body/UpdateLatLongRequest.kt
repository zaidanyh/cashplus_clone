package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class UpdateLatLongRequest(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("lat")
	val lat: Double,

	@field:SerializedName("long")
	val long: Double
)
