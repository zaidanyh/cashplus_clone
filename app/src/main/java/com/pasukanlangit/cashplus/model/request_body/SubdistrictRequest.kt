package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class SubdistrictRequest(

	@field:SerializedName("regency_id")
	val regencyId: String,

	@field:SerializedName("uuid")
	val uuid: String
)
