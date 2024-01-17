package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class TrxResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("trx_id")
	val trxId: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("quantity")
	val quantity: String?,

	@field:SerializedName("bill_data")
	val billData: TrxBillData?,

	@field:SerializedName("short_dsc")
	val shortDsc: String,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("dsc")
	val dsc: String,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("fee")
	val fee: String,

	@field:SerializedName("opr_name")
	val oprName: String,

	@field:SerializedName("rc_msg")
	val rcMsg: String,

	@field:SerializedName("kode_produk")
	val kodeProduk: String
)

data class TrxBillData(

	@field:SerializedName("lembar")
	val lembar: String,

	@field:SerializedName("total")
	val total: String,

	@field:SerializedName("tagihan")
	val tagihan: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("admin")
	val admin: String,

	@field:SerializedName("periode")
	val periode: String,

	@field:SerializedName("info")
	val info: String,

	//this is for tag gold
	@field:SerializedName("ppn_11_persen")
	val ppn11Persen: String,

	@field:SerializedName("pph_22")
	val pph22: String,

	@field:SerializedName("potongan_harga")
	val discountFee: String?,

	//this is for tag withdraw
	@field:SerializedName("biaya_sertifikat")
	val certificateFee: String?,

	@field:SerializedName("biaya_pengiriman")
	val sendFee: String?,

	@field:SerializedName("asuransi")
	val assuranceFee: String?,
)
