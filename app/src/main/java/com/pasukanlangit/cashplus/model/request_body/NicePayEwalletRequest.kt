package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class NicePayEwalletRequest(

	@field:SerializedName("bank_mitra_code")
	val bankMitraCode: String? = null,

	@field:SerializedName("billing_phone")
	var billingPhone: String? = null,

	@field:SerializedName("amt")
	var amt: String? = null,

	@field:SerializedName("pay_method")
	val payMethod: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null,

	val payName: String? = null
)
