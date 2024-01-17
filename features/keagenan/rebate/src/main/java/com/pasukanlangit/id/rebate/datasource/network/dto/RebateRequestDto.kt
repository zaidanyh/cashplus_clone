package com.pasukanlangit.id.rebate.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RebateRequestDto(

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("date_end")
	val dateEnd: String,

	@field:SerializedName("uuid")
	val uuid: String
)
