package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class OrderCheckTokoRequest(

	@field:SerializedName("trx_id")
	val trxId: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("order_id")
	val orderId: String,

	@field:SerializedName("is_show_fee")
	val isShowFee: String? = null
)
