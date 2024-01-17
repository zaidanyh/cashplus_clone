package com.pasukanlangit.id.rebate.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RebateResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("date_end")
	val dateEnd: String,

	@field:SerializedName("total_rebate")
	val totalRebate: String,

	@field:SerializedName("uuid")
	val uuid: String
)
