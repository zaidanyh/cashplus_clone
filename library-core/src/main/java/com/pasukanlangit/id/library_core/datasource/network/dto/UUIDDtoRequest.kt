package com.pasukanlangit.id.library_core.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class UUIDDtoRequest(

	@field:SerializedName("uuid")
	val uuid: String
)
