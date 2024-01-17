package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ServerIdGamesResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: List<ServerIdDataItem>
)

data class ServerIdDataItem(

	@field:SerializedName("server_name")
	val serverName: String,

	@field:SerializedName("kode_provider")
	val kodeProvider: String,

	@field:SerializedName("server_id")
	val serverId: String
)
