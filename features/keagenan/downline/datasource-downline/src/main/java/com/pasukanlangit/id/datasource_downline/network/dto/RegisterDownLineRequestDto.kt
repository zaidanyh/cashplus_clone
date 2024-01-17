package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class RegisterDownLineRequestDto(

	@field:SerializedName("uuid")
	val uuid: String,
	@field:SerializedName("phone")
	val phone: String,
	@field:SerializedName("full_name")
	val fullName: String,
	@field:SerializedName("owner_name")
	val ownerName: String,
	@field:SerializedName("email")
	val email: String,
	@field:SerializedName("pass")
	val password: String,
	@field:SerializedName("address")
	val address: String,
	@field:SerializedName("prov")
	val prov: String,
	@field:SerializedName("city")
	val city: String,
	@field:SerializedName("district")
	val district: String,
	@field:SerializedName("village")
	val village: String,
	@field:SerializedName("zipcode")
	val zipcode: String
)
