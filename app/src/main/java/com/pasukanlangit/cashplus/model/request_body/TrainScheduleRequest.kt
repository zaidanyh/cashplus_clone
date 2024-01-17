package com.pasukanlangit.cashplus.model.request_body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainScheduleRequest(

	@field:SerializedName("date")
	var date: String,

	@field:SerializedName("from")
	val from: String,

	@field:SerializedName("to")
	val to: String,

	@field:SerializedName("adult")
	val adult: String,

	@field:SerializedName("infant")
	val infant: String,

	@field:SerializedName("uuid")
	val uuid: String
) : Parcelable
