package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class QrMpmResponseDto(

	@field:SerializedName("responseCode")
	val responseCode: String,

	@field:SerializedName("responseMessage")
	val responseMessage: String,

	@field:SerializedName("referenceNo")
	val referenceNo: String,

	@field:SerializedName("partnerReferenceNo")
	val partnerReferenceNo: String,

	@field:SerializedName("redirectUrl")
	val redirectUrl: String,

	@field:SerializedName("merchantName")
	val merchantName: String,

	@field:SerializedName("merchantCategory")
	val merchantCategory: String,

	@field:SerializedName("merchantLocation")
	val merchantLocation: String,

	@field:SerializedName("currency")
	val currency: String,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfoQr,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("msg")
	val msg: String
)

data class AdditionalInfoQr(
	val any: Any? = null
)
