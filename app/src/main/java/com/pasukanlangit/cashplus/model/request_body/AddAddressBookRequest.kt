package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class AddAddressBookRequest(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("address_type")
	val addressType: String,

	@field:SerializedName("province_id")
	val provinceId: String,

	@field:SerializedName("pos_code")
	val posCode: String,

	@field:SerializedName("receiver_name")
	val receiverName: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("is_main_address")
	val isMainAddress: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("city_id")
	val cityId: String,

	@field:SerializedName("subdistrict_id")
	val subdistrictId: String,

	//optional just for http method update
	val book_address_id : String = ""

)
