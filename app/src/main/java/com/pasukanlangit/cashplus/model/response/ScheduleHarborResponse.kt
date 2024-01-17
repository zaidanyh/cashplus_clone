package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ScheduleHarborResponse(

	@field:SerializedName("endOfData")
	val endOfData: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: ScheduleHarborData,

	@field:SerializedName("message")
	val message: String
)

data class ScheduleHarborData(

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("schedule")
	val schedule: List<ScheduleHarborItem>
)

@Parcelize
data class ScheduleHarborItem(

	@field:SerializedName("ship_from")
	val shipFrom: String,

	@field:SerializedName("ship_inforoute")
	val shipInforoute: String,

	@field:SerializedName("ship_admin")
	val shipAdmin: Int,

	@field:SerializedName("ship_basicfare")
	val shipBasicfare: Int,

	@field:SerializedName("ship_route")
	val shipRoute: String,

	@field:SerializedName("ship_class")
	val shipClass: String,

	@field:SerializedName("ship_datetime")
	val shipDatetime: String,

	@field:SerializedName("ship_price")
	val shipPrice: Int,

	@field:SerializedName("ship_femaleseat")
	val shipFemaleseat: String,

	@field:SerializedName("ship_number")
	val shipNumber: String,

	@field:SerializedName("ship_name")
	val shipName: String,

	@field:SerializedName("ship_infodatetime")
	val shipInfodatetime: String,

	@field:SerializedName("ship_maleseat")
	val shipMaleseat: String,

	@field:SerializedName("ship_code")
	val shipCode: String,

	@field:SerializedName("ship_to")
	val shipTo: String,

	@field:SerializedName("ship_date")
	val shipDate: String
) : Parcelable
