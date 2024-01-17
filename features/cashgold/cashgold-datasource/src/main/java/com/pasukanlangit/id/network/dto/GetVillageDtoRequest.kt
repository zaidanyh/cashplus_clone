package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class GetVillageDtoRequest(

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("City")
	val city: String,

	@field:SerializedName("uuid")
	val uuid: String
)
