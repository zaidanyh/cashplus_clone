package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ReferralChangeRequest(

	@field:SerializedName("referral")
	val referral: String? = null,

	@field:SerializedName("uuid")
	val uuid: String? = null
)