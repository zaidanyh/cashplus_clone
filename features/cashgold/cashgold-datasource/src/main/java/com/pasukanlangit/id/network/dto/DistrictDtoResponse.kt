package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class DistrictDtoResponse(

	@field:SerializedName("data")
	val data: DistrictDtoData
)

data class DistrictDataItem(

	@field:SerializedName("kecamatan")
	val district: String
)

data class DistrictDtoData(

	@field:SerializedName("data")
	val provinceData: List<DistrictDataItem>
)
