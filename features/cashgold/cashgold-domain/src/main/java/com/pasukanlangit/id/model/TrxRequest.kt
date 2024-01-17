package com.pasukanlangit.id.model

data class TrxRequest(
	val pin: String,
	val dest: String,
	val kodeProduk: String,
	val uuid: String
)
