package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class CourierPriceRequest(

	@field:SerializedName("courier")
	val courier: String,

	@field:SerializedName("destination")
	val destination: String,

	@field:SerializedName("weight")
	val weight: String,

	@field:SerializedName("uuid")
	val uuid: String
)
