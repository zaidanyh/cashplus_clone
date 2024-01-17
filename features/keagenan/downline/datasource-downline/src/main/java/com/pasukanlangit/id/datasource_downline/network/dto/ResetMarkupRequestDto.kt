package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class ResetMarkupRequestDto(

	@field:SerializedName("markup")
	val markup: String,

	@field:SerializedName("uuid")
	val uuid: String
)
