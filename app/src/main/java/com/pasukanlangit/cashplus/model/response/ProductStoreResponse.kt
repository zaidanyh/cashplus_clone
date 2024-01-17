package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ProductStoreResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<ProductStoreDataItem>,

	@field:SerializedName("message")
	val message: String
)

@Entity(tableName = "product_cart")
@Parcelize
data class ProductStoreDataItem(

	@field:SerializedName("viewer")
	var viewer: String = "",

	@field:SerializedName("image3")
	var image3: String = "",

	@field:SerializedName("image4")
	var image4: String = "",

	@field:SerializedName("time_start")
	var timeStart: String = "",

	@field:SerializedName("product_condition")
	var productCondition: String = "",

	@field:SerializedName("rate_average")
	var rateAverage: String = "",

	@field:SerializedName("description")
	var description: String = "",

	@field:SerializedName("weight")
	var weight: String = "",

	@field:SerializedName("discount")
	var discount: String = "",

	@field:SerializedName("row_num")
	var rowNum: String = "",

	@field:SerializedName("image1")
	var image1: String = "",

	@field:SerializedName("product_name")
	var productName: String = "",

	@field:SerializedName("image2")
	var image2: String = "",

	@field:SerializedName("product_category_name")
	var productCategoryName: String = "",

	@field:SerializedName("price")
	var price: String = "",

	@PrimaryKey
	@field:SerializedName("product_id")
	var productId: String = "",

	@field:SerializedName("stock")
	var stock: String = "",

	@field:SerializedName("brand")
	var brand: String = "",

	//minimum buy 1
	var qty: Int = 1,
	var isChecked: Boolean = false,
	var note : String = "",
	var userId: String = ""
) : Parcelable
