package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class CategoryProductResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val categories: List<CategoryProductDataItem>
)

data class CategoryProductDataItem(

	@field:SerializedName("product_category_name")
	val name: String,

	@field:SerializedName("product_category_id")
	val id: String
)
