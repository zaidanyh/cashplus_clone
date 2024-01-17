package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class CityRequestDto(

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
