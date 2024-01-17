package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class KYCRequest(

	@field:SerializedName("base64_data")
	val base64Data: String,

	@field:SerializedName("upload_type")
	val uploadType: String,

	@field:SerializedName("file_ext")
	val fileExt: String,

	@field:SerializedName("id_type")
	val idType: String,

	@field:SerializedName("uuid")
	val uuid: String
)
