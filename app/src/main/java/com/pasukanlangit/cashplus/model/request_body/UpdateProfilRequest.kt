package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class UpdateProfilRequest(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("prov")
	val prov: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("village")
	val village: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("zipcode")
	val zipcode: String,

	@field:SerializedName("nik")
	val nik: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("owner_name")
	val owner: String?,

	@field:SerializedName("tempat_lahir")
	val placeOfBorn: String,

	@field:SerializedName("tgl_lahir")
	val dateOfBirth: String
)
