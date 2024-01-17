package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class AllAddressBookResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<AddressBookData>,

	@field:SerializedName("message")
	val message: String
)
