package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class TopUpResponseDto(

	@field:SerializedName("responseCode")
	val responseCode: String,

	@field:SerializedName("responseMessage")
	val responseMessage: String,

	@field:SerializedName("referenceNo")
	val referenceNo: String?,

	@field:SerializedName("partnerReferenceNo")
	val partnerReferenceNo: String,

	@field:SerializedName("sessionId")
	val sessionId: String?,

	@field:SerializedName("customerNumber")
	val customerNumber: String,

	@field:SerializedName("referenceNumber")
	val referenceNumber: String?,

	@field:SerializedName("amount")
	val amount: BalanceAmount,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfoTopUp,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("originalExternalId")
	val originalExternalId: String,

	@field:SerializedName("originalPartnerReferenceNo")
	val originalPartnerReferenceNo: String,

	@field:SerializedName("transactionDate")
	val transactionDate: String
)

data class AdditionalInfoTopUp(

	@field:SerializedName("transactionNumber")
	val transactionNumber: String
)
