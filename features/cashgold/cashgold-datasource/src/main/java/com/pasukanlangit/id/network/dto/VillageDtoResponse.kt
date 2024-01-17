package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class VillageDtoResponse(

	@field:SerializedName("data")
	val data: VillageDtoData
)

data class VillageDataItem(
	@field:SerializedName("kelurahan")
	val village: String,
	@field:SerializedName("kodepos")
	val code: String
)

data class VillageDtoData(

	@field:SerializedName("data")
	val provinceData: List<VillageDataItem>
)
