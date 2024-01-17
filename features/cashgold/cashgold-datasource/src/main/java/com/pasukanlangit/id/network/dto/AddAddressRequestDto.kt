package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class AddAddressRequestDto(

	@field:SerializedName("zipCode")
	val zipCode: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("village")
	val village: String,

	@field:SerializedName("uuid")
	val uuid: String
)
