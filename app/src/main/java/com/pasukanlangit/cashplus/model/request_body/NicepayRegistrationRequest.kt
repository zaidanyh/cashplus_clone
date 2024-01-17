package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class NicepayRegistrationRequest(

	@field:SerializedName("bank_mitra_code")
	val bankMitraCode: String,

	@field:SerializedName("pay_method")
	val payMethod: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("amt")
	val amt: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	val payName: String ?= null
)
