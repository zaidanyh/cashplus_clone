package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DistrictRequestDto(

	@field:SerializedName("regency_id")
	val regencyId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
