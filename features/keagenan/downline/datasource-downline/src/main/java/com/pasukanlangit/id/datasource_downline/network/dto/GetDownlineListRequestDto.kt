package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class GetDownlineListRequestDto(

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("downline_phone")
	val downlinePhone: String?,

	@field:SerializedName("date_end")
	val dateEnd: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("row_start")
	val rowStart: String
)
