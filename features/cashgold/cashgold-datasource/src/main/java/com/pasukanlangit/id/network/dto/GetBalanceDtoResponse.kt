package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class GetBalanceDtoResponse(
	@field:SerializedName("data")
	val data: GetBalanceData
)

data class GetBalanceData(

	@field:SerializedName("data")
	val data: GoldData?,

	@field:SerializedName("error")
	val error: Error?,
)

data class GoldData(
	@field:SerializedName("gold")
	val gold: Double,

	@field:SerializedName("indogold_gold")
	val indogoldGold: Double
)
//
//data class ErrorsItem(
//
//	@field:SerializedName("code")
//	val code: Int,
//
//	@field:SerializedName("message")
//	val message: String
//)
//
//data class Error(
//
//	@field:SerializedName("code")
//	val code: Int,
//
//	@field:SerializedName("message")
//	val message: String,
//
//	@field:SerializedName("errors")
//	val errors: List<ErrorsItem>?
//)
