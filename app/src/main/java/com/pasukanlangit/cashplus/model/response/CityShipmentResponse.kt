package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class CityShipmentResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<CityShipmentDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class CityShipmentDataItem(

	@field:SerializedName("city_name")
	val cityName: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("postal_code")
	val postalCode: String,

	@field:SerializedName("city_id")
	val cityId: String
)
