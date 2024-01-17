package com.pasukanlangit.id.rebate.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class RebateProductDetailResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<RebateProductDetailDataItem>
)

data class RebateProductDetailDataItem(

	@field:SerializedName("total_rebate")
	val totalRebate: String,

	@field:SerializedName("kode_produk")
	val kodeProduk: String
)
