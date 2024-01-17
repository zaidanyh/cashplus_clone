package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ProvinceShipmentResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<ProfinceShipmentDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class ProfinceShipmentDataItem(

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("province_id")
	val provinceId: String
)
