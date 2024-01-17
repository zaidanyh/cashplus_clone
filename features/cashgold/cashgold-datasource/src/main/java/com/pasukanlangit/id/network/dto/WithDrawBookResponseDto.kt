package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class WithDrawBookResponseDto(
	val rc: String?,

	@field:SerializedName("data")
	val data: WithDrawBookData?,
)

data class WithDrawBookData(

	@field:SerializedName("withdrawId")
	val withdrawId: String
)
