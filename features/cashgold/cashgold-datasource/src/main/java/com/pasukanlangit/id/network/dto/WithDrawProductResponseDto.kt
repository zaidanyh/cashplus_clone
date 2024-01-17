package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class WithDrawProductResponseDto(

	@field:SerializedName("data")
	val dataWithDraw: DataWithDraw?
)

data class WithDrawProvider(

	@field:SerializedName("price")
	val price: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("logo")
	val logo: String,

	@field:SerializedName("id")
	val id: Int
)

data class Denomination(

	@field:SerializedName("gram")
	val gram: String,

	@field:SerializedName("raw")
	val raw: Double
)

data class WithDrawDetailsItem(

	@field:SerializedName("total_withdraw_daily")
	val totalWithdrawDaily: Int,

	@field:SerializedName("stock_amount")
	val stockAmount: Int,

	@field:SerializedName("certificate_fee")
	val certificateFee: CertificateFee,

	@field:SerializedName("denomination")
	val denomination: Denomination,

	@field:SerializedName("daily_withdraw_limit")
	val dailyWithdrawLimit: Int
)

data class CertificateFee(

	@field:SerializedName("idr")
	val idr: String,

	@field:SerializedName("raw")
	val raw: Long
)

data class WithDrawProductItem(

	@field:SerializedName("product")
	val providers: WithDrawProvider,

	@field:SerializedName("details")
	val products: List<WithDrawDetailsItem>?
)

data class DataWithDraw(

	@field:SerializedName("data")
	val product: List<WithDrawProductItem>?
)
