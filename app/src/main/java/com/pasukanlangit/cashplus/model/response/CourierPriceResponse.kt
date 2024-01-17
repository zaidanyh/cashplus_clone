package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class CourierPriceResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<CourierPriceDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class CostItem(

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("etd")
	val etd: String,

	@field:SerializedName("value")
	val value: Int
)

data class CostsItem(

	@field:SerializedName("cost")
	val cost: List<CostItem>,

	@field:SerializedName("service")
	val service: String,

	@field:SerializedName("description")
	val description: String,

	var prefix : String = ""
)

data class CourierPriceDataItem(

	@field:SerializedName("costs")
	val costs: List<CostsItem>,

	@field:SerializedName("code")
	val code: String,

	@field:SerializedName("name")
	val name: String
)
