package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class VillageRequestDto(

	@field:SerializedName("district_id")
	val districtId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
