package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionTAGResponse(
	@field:SerializedName("phone")
	val phone: String,
	@field:SerializedName("rc")
	val rc: String,
	@field:SerializedName("msg")
	val msg: String,
	@field:SerializedName("kode_produk")
	val kode_produk: String,
	@field:SerializedName("dsc")
	val dsc: String,
	@field:SerializedName("price")
	val price: String?,
	@field:SerializedName("fee")
	val fee: String?,
	@field:SerializedName("bill_data")
	val billData: BillData,
	@field:SerializedName("reqid")
	val reqid: String ?= null,
) : Parcelable

@Parcelize
data class BillData(
	@field:SerializedName("rc")
	val rc: String?,
	@field:SerializedName("nama")
	val nama: String,
	@field:SerializedName("tagihan")
	val tagihan: String,
	@field:SerializedName("lembar")
	val lembar: String,
	@field:SerializedName("admin")
	val admin: String,
	@field:SerializedName("total")
	val total: String,
	@field:SerializedName("periode")
	val periode: String?,
	@field:SerializedName("info")
	var info: String,
	@field:SerializedName("orig_info")
	var originInfo: String?,
	var payCode : String?=null
) : Parcelable
