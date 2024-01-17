package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class SubDownlineListResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("my_trx_count")
	val myTrxCount: String,

	@field:SerializedName("parent_user")
	val parentUser: String,

	@field:SerializedName("data")
	val data: List<SubDownLineDataItem>,

	@field:SerializedName("my_balance")
	val myBalance: String,

	@field:SerializedName("user")
	val user: String
)

data class SubDownLineDataItem(

	@field:SerializedName("owner_name")
	val ownerName: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("is_active")
	val isActive: String,

	@field:SerializedName("markup")
	val markup: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("phones")
	val phones: List<PhonesItem>?,

	@field:SerializedName("markup_per_product")
	val markupPerProduct: List<MarkupPerProductItem>,

	@field:SerializedName("zipcode")
	val zipcode: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("balance")
	val balance: String,

	@field:SerializedName("downline_count")
	val downlineCount: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("village")
	val village: String,

	@field:SerializedName("user")
	val user: String,

	@field:SerializedName("prov")
	val prov: String,

	@field:SerializedName("trx_count")
	val trxCount: String,

	@field:SerializedName("email")
	val email: String
)

