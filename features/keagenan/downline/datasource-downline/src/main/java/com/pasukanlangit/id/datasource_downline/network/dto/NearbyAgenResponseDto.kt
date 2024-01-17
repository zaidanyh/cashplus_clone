package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class NearbyAgenResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("my_long")
	val myLong: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<NearbyAgentDataItem>,

	@field:SerializedName("my_lat")
	val myLat: String
)

data class NearbyAgentDataItem(

	@field:SerializedName("district_name")
	val districtName: String,

	@field:SerializedName("city_name")
	val cityName: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("distance")
	val distance: String,

	@field:SerializedName("prov_name")
	val provName: String,

	@field:SerializedName("ref_code")
	val refCode: String,

	@field:SerializedName("village_name")
	val villageName: String,

	@field:SerializedName("lat")
	val lat: String,

	@field:SerializedName("long")
	val long: String
)
