package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("email")
	val email: String
)
