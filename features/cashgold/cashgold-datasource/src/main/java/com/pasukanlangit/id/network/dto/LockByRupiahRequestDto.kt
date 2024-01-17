package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class LockByRupiahRequestDto(

	@field:SerializedName("rupiahNominal")
	val rupiahNominal: String,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("uuid")
	val uuid: String
)
