package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TrainPriceRequest(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("session")
	val session: String,

	@field:SerializedName("train_subclass")
	val trainSubclass: String,

	@field:SerializedName("from")
	val from: String,

	@field:SerializedName("to")
	val to: String,

	@field:SerializedName("train_code")
	val trainCode: String,

	@field:SerializedName("adult")
	val adult: String,

	@field:SerializedName("infant")
	val infant: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("train_class")
	val trainClass: String
)
