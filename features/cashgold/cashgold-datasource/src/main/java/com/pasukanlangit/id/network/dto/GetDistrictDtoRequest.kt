package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class GetDistrictDtoRequest(

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("uuid")
	val uuid: String
)
