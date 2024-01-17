package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class SubdistrictShipmentResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<SubdistrictShipmentDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class SubdistrictShipmentDataItem(

	@field:SerializedName("subdistrict_id")
	val subdistrictId: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("subdistrict_name")
	val subdistrictName: String,

	@field:SerializedName("city_id")
	val cityId: String
)
