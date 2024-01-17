package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class CityDtoResponse(

	@field:SerializedName("data")
	val data: CityDtoData
)

data class CityDataItem(

	@field:SerializedName("kabupaten")
	val city: String
)

data class CityDtoData(

	@field:SerializedName("data")
	val provinceData: List<CityDataItem>
)
