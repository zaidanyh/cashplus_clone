package com.pasukanlangit.id.cash_transfer.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TransferTransactionResponseDto(
	@field:SerializedName("phone")
	val phone: String? = null,
	@field:SerializedName("rc")
	val rc: String? = null,
	@field:SerializedName("msg")
	val msg: String? = null,
	@field:SerializedName("rc_msg")
	val rcMsg: String? = null,
	@field:SerializedName("trx_id")
	val trxId: String? = null,
	@field:SerializedName("kode_produk")
	val kodeProduk: String? = null,
	@field:SerializedName("dsc")
	val dsc: String? = null,
	@field:SerializedName("short_dsc")
	val shortDsc: String? = null,
	@field:SerializedName("opr_name")
	val oprName: String? = null,
	@field:SerializedName("fee")
	val fee: String? = null,
	@field:SerializedName("price")
	val price: String? = null,
	@field:SerializedName("bill_data")
	val billData: BillDataTf? = null
)

data class BillDataTf(
	@field:SerializedName("header")
	val header: String? = null,
	@field:SerializedName("no_rek")
	val noRek: String? = null,
	@field:SerializedName("nama")
	val nama: String? = null,
	@field:SerializedName("bank")
	val bank: String? = null,
	@field:SerializedName("lembar")
	val lembar: String? = null,
	@field:SerializedName("tagihan")
	val tagihan: String? = null,
	@field:SerializedName("admin")
	val admin: String? = null,
	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("orig_info")
	val origInfo: String? = null,
	@field:SerializedName("berita")
	val berita: String? = null,
	@field:SerializedName("footer")
	val footer: String? = null,
	@field:SerializedName("info")
	val info: String? = null
)
