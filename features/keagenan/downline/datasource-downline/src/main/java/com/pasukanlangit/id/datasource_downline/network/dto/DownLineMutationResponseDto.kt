package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownLineMutationResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<DownLineMutationDataItem>
)

data class DownLineMutationDataItem(
	
	@field:SerializedName("no_hp")
	val noHp: String,

	@field:SerializedName("trans_stat")
	val transStat: String,

	@field:SerializedName("dsc")
	val dsc: String,

	@field:SerializedName("ending_balance")
	val endingBalance: String,

	@field:SerializedName("row_num")
	val rowNum: String,

	@field:SerializedName("value")
	val value: String,

	@field:SerializedName("kode_produk")
	val kodeProduk: String,

	@field:SerializedName("trx_date")
	val trxDate: String,

	@field:SerializedName("db_cr")
	val dbCr: String,

	@field:SerializedName("trans_stat_dsc")
	val transStatDsc: String
)
