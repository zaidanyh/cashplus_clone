package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class DeleteAddressRequestDto(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("addressId")
	val addressId: String
)
