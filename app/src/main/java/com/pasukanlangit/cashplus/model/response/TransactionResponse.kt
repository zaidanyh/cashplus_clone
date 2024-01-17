package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class TransactionResponse(

	@field:SerializedName("msg")
	val msg: String?,

	@field:SerializedName("trx_id")
	val trxId: String?,

	@field:SerializedName("rc")
	val rc: String?,

	@field:SerializedName("balance")
	val balance: String?,

	@field:SerializedName("phone")
	val phone: String?,

	@field:SerializedName("price")
	val price: String?,

	@field:SerializedName("rc_msg")
	val rcMsg: String?,

	@field:SerializedName("sn")
	val sn: String?,

	@field:SerializedName("dest")
	val dest: String?,

	@field:SerializedName("reqid")
	val reqid: String?,

	@field:SerializedName("dsc")
	val desc: String?,

	@field:SerializedName("kode_produk")
	val kode_produk: String?
)
