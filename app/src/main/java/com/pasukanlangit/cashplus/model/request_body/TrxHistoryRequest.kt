package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class TrxHistoryRequest(

	@field:SerializedName("uuid")
	var uuid: String? = null,

	@field:SerializedName("date_start")
	var dateStart: String? = null,

	@field:SerializedName("date_end")
	var dateEnd: String? = null,

	@field:SerializedName("row_start")
	val rowStart: String? = null
)
