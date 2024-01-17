package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class ProvinceDtoResponse(

	@field:SerializedName("data")
	val data: ProvinceDtoData
)

data class ProvinceDataItem(

	@field:SerializedName("provinsi")
	val provinsi: String
)

data class ProvinceDtoData(

	@field:SerializedName("data")
	val provinceData: List<ProvinceDataItem>
)
