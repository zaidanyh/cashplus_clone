package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownlineSummaryTransferResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("downline_phone")
	val downlinePhone: String,

	@field:SerializedName("data")
	val data: List<DownlineSummaryDataItem>,

	@field:SerializedName("date_end")
	val dateEnd: String
)

data class DownlineSummaryDataItem(

	@field:SerializedName("downline_full_name")
	val downlineFullName: String,

	@field:SerializedName("user_name")
	val userName: String,

	@field:SerializedName("total_transfer")
	val totalTransfer: String
)
