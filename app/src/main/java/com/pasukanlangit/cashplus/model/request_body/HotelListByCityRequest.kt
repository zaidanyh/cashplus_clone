package com.pasukanlangit.cashplus.model.request_body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotelListByCityRequest(

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("hotel_city_code")
	val hotelCityCode: String,

	@field:SerializedName("hotel_city_name")
	val hotelCityName: String,

	@field:SerializedName("hotel_code")
	val hotelCode: String,

	@field:SerializedName("hotel_name")
	val hotelName: String,

	@field:SerializedName("full_address")
	val fullAddress: String?
) : Parcelable
