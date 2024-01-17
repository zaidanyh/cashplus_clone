package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class GetCityDtoRequest(

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("uuid")
	val uuid: String
)
