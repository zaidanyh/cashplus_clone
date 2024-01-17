package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class DownlineListResponseDto(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("page_size")
	val pageSize: Int?,

	@field:SerializedName("page_index")
	val pageIndex: Int?,

	@field:SerializedName("page_count")
	val pageCount: Int?,

	@field:SerializedName("can_next_page")
	val canNextPage: Boolean?,

	@field:SerializedName("can_previous_page")
	val canPreviousPage: Boolean?,

	@field:SerializedName("user")
	val user: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("my_balance")
	val myBalance: String,

	@field:SerializedName("my_trx_count")
	val myTrxCount: String,

	@field:SerializedName("downline_count")
	val downlineCount: String,

	@field:SerializedName("downline_total_balance")
	val downlineTotalBalance: String,

	@field:SerializedName("data")
	val data: List<DownlineDataItem>?
)

data class DownlineDataItem(

	@field:SerializedName("user")
	val user: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("owner_name")
	val ownerName: String,

	@field:SerializedName("kode_markup_plan")
	val codeMarkup: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("prov")
	val prov: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("village")
	val village: String,

	@field:SerializedName("zipcode")
	val zipcode: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("balance")
	val balance: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("is_active")
	val isActive: String,

	@field:SerializedName("markup")
	val markup: String,

	@field:SerializedName("phones")
	val phones: List<PhonesItem>,

	@field:SerializedName("markup_per_product")
	val markupPerProduct: List<MarkupPerProductItem>,

	@field:SerializedName("downline_count")
	val downlineCount: String,

	@field:SerializedName("trx_count")
	val trxCount: String
)

data class PhonesItem(
	@field:SerializedName("phone")
	val phone: String
)

data class MarkupPerProductItem(

	@field:SerializedName("markup")
	val markup: String,

	@field:SerializedName("kode_produk")
	val kodeProduk: String
)
