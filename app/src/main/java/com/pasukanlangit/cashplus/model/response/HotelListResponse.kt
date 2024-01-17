package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class HotelListResponse(

	@field:SerializedName("endOfData")
	val endOfData: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<HotelListDataItem>?,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class HotelListDataItem(

	@field:SerializedName("hotel_image_url_1")
	val hotelImageUrl1: String?,

	@field:SerializedName("hotel_image_url_2")
	val hotelImageUrl2: String?,

	@field:SerializedName("hotel_image_url_5")
	val hotelImageUrl5: String,

	@field:SerializedName("hotel_image_url_3")
	val hotelImageUrl3: String,

	@field:SerializedName("hotel_image_url_4")
	val hotelImageUrl4: String,

	@field:SerializedName("hotel_city_name")
	val hotelCityName: String,

	@field:SerializedName("hotel_code")
	val hotelCode: String,

	@field:SerializedName("hotel_country_code")
	val hotelCountryCode: String,

	@field:SerializedName("hotel_country_name")
	val hotelCountryName: String,

	@field:SerializedName("hotel_city_code")
	val hotelCityCode: String,

	@field:SerializedName("hotel_name")
	val hotelName: String,

	@field:SerializedName("hotel_supplier_code")
	val hotelSupplierCode: String
) : Parcelable
