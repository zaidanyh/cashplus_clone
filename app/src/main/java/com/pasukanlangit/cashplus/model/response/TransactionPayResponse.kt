package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionPayResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("bill_data")
	val billData: BillDataPayTrx? = null,

	@field:SerializedName("short_dsc")
	val shortDsc: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("dsc")
	val dsc: String? = null,

	@field:SerializedName("opr_name")
	val oprName: String? = null,

	@field:SerializedName("kode_produk")
	val kodeProduk: String? = null,

	val rc_msg : String ?= null,
) : Parcelable
//

@Parcelize
data class BillDataPayTrx(

//	@field:SerializedName("rc")
//	val rc: String? = null,
//
//	@field:SerializedName("lembar")
//	val lembar: String? = null,
//
//	@field:SerializedName("total")
//	val total: String? = null,
//
//	@field:SerializedName("ref")
//	val ref: String? = null,
//
//	@field:SerializedName("url_struk")
//	val urlStruk: String? = null,
//
//	@field:SerializedName("tagihan")
//	val tagihan: String? = null,
//
//	@field:SerializedName("nama")
//	val nama: String? = null,
//
//	@field:SerializedName("admin")
//	val admin: String? = null,
//
//	@field:SerializedName("periode")
//	val periode: String? = null,

	@field:SerializedName("info")
	val info: String
) : Parcelable
