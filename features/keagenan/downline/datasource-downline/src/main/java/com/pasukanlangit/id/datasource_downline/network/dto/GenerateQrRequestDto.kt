package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class GenerateQrRequestDto(

	@field:SerializedName("amount")
	val amount: String,

	@field:SerializedName("uuid")
	val uuid: String
)
