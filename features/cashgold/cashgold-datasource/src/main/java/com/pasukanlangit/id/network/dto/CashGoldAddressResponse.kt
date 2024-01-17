package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class CashGoldAddressResponse(
	@field:SerializedName("data")
	val data: CashGoldAddressData,
)

data class CashGoldAddressData(

	@field:SerializedName("data")
	val data: AddressData
)

data class AddressData(
	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("isAddedable")
	val isAddedable: Boolean,

	@field:SerializedName("address")
	val address: List<AddressItem>,
)

data class AddressItem(

	@field:SerializedName("provinsi")
	val provinsi: String,

	@field:SerializedName("keterangan")
	val keterangan: String,

	@field:SerializedName("kecamatan")
	val kecamatan: String,

	@field:SerializedName("kodepos")
	val kodepos: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("is_default")
	val isDefault: Boolean,

	@field:SerializedName("kabupaten")
	val kabupaten: String,

	@field:SerializedName("kelurahan")
	val kelurahan: String,

	@field:SerializedName("alamat")
	val alamat: String
)
