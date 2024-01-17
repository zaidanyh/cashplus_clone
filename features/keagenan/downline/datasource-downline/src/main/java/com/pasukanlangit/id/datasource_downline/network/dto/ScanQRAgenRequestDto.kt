package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class ScanQRAgenRequestDto(

	@field:SerializedName("pin")
	val pin: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("uuid")
	val uuid: String
)
