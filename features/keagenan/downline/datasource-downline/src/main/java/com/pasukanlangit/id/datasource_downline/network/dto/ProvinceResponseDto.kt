package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class ProvinceResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<ProvinceDataItem>? = null
)

data class ProvinceDataItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
