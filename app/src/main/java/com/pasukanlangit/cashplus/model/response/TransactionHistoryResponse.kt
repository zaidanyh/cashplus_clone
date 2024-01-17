package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TransactionHistoryResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("data")
	val data: List<TransactionItem>? = null
)

@Parcelize
data class TransactionItem(
	@field:SerializedName("row_num")
	val rowNum: String? = null,

	@field:SerializedName("trx_id")
	val trxId: String? = null,

	@field:SerializedName("reqid")
	val reqId: String? = null,

	@field:SerializedName("trx_start")
	val trxStart: String? = null,

	@field:SerializedName("trx_finish")
	val trxFinish: String? = null,

	@field:SerializedName("kode_produk")
	val productCode: String? = null,

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("trans_stat")
	val transStat: String? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("value")
	val value: String? = null,

	@field:SerializedName("ending_balance")
	val endingBalance: String? = null,

	@field:SerializedName("sn")
	val sn: String? = null,

	@field:SerializedName("response_url")
	val url: String? = null,

	@field:SerializedName("kode_provider")
	val providerCode: String? = null,

	@field:SerializedName("dsc")
	val dsc: String? = null,

	@field:SerializedName("short_dsc")
	val shortDsc: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("fee")
	val fee: String? = null
) : Parcelable
