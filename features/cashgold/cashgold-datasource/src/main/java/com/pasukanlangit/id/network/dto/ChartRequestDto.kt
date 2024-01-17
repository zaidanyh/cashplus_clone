package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class ChartRequestDto(

	@field:SerializedName("numberOfDays")
	val numberOfDays: String,

	@field:SerializedName("uuid")
	val uuid: String
)
