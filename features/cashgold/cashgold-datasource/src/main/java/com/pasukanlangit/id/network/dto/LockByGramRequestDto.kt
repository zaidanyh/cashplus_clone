package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class LockByGramRequestDto(

	@field:SerializedName("gramNominal")
	val gramNominal: String,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("uuid")
	val uuid: String
)
