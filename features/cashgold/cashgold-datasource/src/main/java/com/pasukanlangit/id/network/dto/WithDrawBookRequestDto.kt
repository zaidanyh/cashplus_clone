package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class WithDrawBookRequestDto(

	@field:SerializedName("quantity")
	val quantity: String,

	@field:SerializedName("productId")
	val productId: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("addressId")
	val addressId: String,

	@field:SerializedName("denomination")
	val denomination: String
)
