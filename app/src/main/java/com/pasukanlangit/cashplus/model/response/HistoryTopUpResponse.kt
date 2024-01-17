package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class HistoryTopUpResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<HistoryTopUpDataItem>?
)

data class HistoryTopUpDataItem(

	@field:SerializedName("row_num")
	val rowNum: String,

	@field:SerializedName("trx_date")
	val trxDate: String,

	@field:SerializedName("payment_method")
	val paymentMethod: String,

	@field:SerializedName("value")
	val value: String,

	@field:SerializedName("ending_balance")
	val endingBalance: String,

	@field:SerializedName("trans_stat")
	val transStat: String,

	@field:SerializedName("no_rek")
	val noRek: String?,

	@field:SerializedName("nama_rek")
	val bank_acc_name: String?
)
