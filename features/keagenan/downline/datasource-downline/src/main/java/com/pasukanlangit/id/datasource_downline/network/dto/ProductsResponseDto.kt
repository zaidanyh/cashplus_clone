package com.pasukanlangit.id.datasource_downline.network.dto

import com.google.gson.annotations.SerializedName

data class ProductsResponseDto(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("is_active")
	val isActive: String? = null,

	@field:SerializedName("data")
	val data: List<DataItem>?,

	@field:SerializedName("kode_provider")
	val kodeProvider: String? = null,

	@field:SerializedName("page_num")
	val pageNum: String? = null,

	@field:SerializedName("opr_name")
	val oprName: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	@field:SerializedName("is_non_markup")
	val isNonMarkup: String? = null,

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("is_faktur")
	val isFaktur: String? = null,

	@field:SerializedName("total_page")
	val totalPage: Int? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("keyword")
	val keyword: Any? = null,

	@field:SerializedName("page_size")
	val pageSize: String? = null
)

data class DataItem(

	@field:SerializedName("is_active")
	val isActive: String,

	@field:SerializedName("min_qty")
	val minQty: String? = null,

	@field:SerializedName("kode_provider")
	val kodeProvider: String,

	@field:SerializedName("admin")
	val admin: String? = null,

	@field:SerializedName("opr_name")
	val oprName: String? = null,

	@field:SerializedName("row_num")
	val rowNum: String? = null,

	@field:SerializedName("kode_produk")
	val kodeProduk: String,

	@field:SerializedName("max_qty")
	val maxQty: String? = null,

	@field:SerializedName("is_non_markup")
	val isNonMarkup: String? = null,

	@field:SerializedName("outlet_price")
	val outletPrice: String,

	@field:SerializedName("img_url_2")
	val imgUrl2: String? = null,

	@field:SerializedName("stock_type")
	val stockType: String? = null,

	@field:SerializedName("short_dsc")
	val shortDsc: String? = null,

	@field:SerializedName("product_type")
	val productType: String? = null,

	@field:SerializedName("dsc")
	val dsc: String? = null,

	@field:SerializedName("img_url")
	val imgUrl: String? = null,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("is_faktur")
	val isFaktur: String? = null,

	@field:SerializedName("category")
	val category: String
)
