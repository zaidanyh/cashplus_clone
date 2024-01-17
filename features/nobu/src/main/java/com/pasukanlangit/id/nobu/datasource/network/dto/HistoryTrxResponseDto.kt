package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class HistoryTrxResponseDto(
	@field:SerializedName("responseCode")
	val responseCode: String,

	@field:SerializedName("responseMessage")
	val responseMessage: String,

	@field:SerializedName("referenceNo")
	val referenceNo: String,

	@field:SerializedName("partnerReferenceNo")
	val partnerReferenceNo: String,

	@field:SerializedName("detailData")
	val detailData: List<DetailDataItem>,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfoHistory,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("msg")
	val msg: String,
)

data class DetailDataItem(

	@field:SerializedName("dateTime")
	val dateTime: String,

	@field:SerializedName("amount")
	val amount: BalanceAmount,

	@field:SerializedName("remark")
	val remark: String,

	@field:SerializedName("sourceOfFunds")
	val sourceOfFunds: String,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfoDetail
)

data class AdditionalInfoHistory(
	@field:SerializedName("totalPage")
	val totalPage: String,
)

data class AdditionalInfoDetail(

	@field:SerializedName("feeAmount")
	val feeAmount: String,

	@field:SerializedName("referenceNo")
	val referenceNo: String,

	@field:SerializedName("receiptNo")
	val receiptNo: String
)
