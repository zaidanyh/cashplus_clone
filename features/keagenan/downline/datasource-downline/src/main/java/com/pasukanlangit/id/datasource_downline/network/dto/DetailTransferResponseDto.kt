package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DetailTransferResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("date_start")
	val dateStart: String,

	@field:SerializedName("downline_phone")
	val downlinePhone: String,

	@field:SerializedName("data")
	val data: List<DetailTransferDataItem>,

	@field:SerializedName("date_end")
	val dateEnd: String
)

data class DetailTransferDataItem(

	@field:SerializedName("downline_full_name")
	val downlineFullName: String,

	@field:SerializedName("trans_stat")
	val transStat: String,

	@field:SerializedName("ending_balance")
	val endingBalance: String,

	@field:SerializedName("row_num")
	val rowNum: String,

	@field:SerializedName("dest")
	val dest: String,

	@field:SerializedName("value")
	val value: String,

	@field:SerializedName("trx_date")
	val trxDate: String,

	@field:SerializedName("payment_method")
	val paymentMethod: String
)
