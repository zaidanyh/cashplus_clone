package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class MessageResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String
)
