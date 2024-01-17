package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class ChartResponseDto(
	@field:SerializedName("data")
	val data: ChartData?,
)

data class ChartData(

	@field:SerializedName("data")
	val data: List<Array<String>>?,

	@field:SerializedName("error")
	val error: Error?
)

