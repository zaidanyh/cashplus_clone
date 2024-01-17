package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownLineMutationRequestDto(

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("is_summary")
	val isSummary: String,

	@field:SerializedName("downline_phone")
	val downlinePhone: String?,

	@field:SerializedName("row_start")
	val rowStart: String,

	@field:SerializedName("date_end")
	val dateEnd: String,

	@field:SerializedName("uuid")
	val uuid: String
)
