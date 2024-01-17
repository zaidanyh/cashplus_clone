package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class GetPriceResponseDto(

	@field:SerializedName("data")
	val data: PriceResponseData?,
)

data class PriceResponseData(

	@field:SerializedName("data")
	val data: PriceDto?,

	@field:SerializedName("error")
	val error: Error?
)

data class PriceDto(
	@field:SerializedName("buy")
	val buy: String,

	@field:SerializedName("sell")
	val sell: String
)
