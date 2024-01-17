package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponseDTO(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("referenceNo")
	val referenceNo: String? = null,

	@field:SerializedName("bankCardToken")
	val bankCardToken: String? = null,

	@field:SerializedName("expiredDatetime")
	val expiredDatetime: String? = null,

	@field:SerializedName("qParams")
	val qParams: QParams? = null,

	@field:SerializedName("cardPan")
	val cardPan: String? = null,

	@field:SerializedName("subscribeDatetime")
	val subscribeDatetime: String? = null,

	@field:SerializedName("phoneNo")
	val phoneNo: String? = null,

	@field:SerializedName("transactionTimestamp")
	val transactionTimestamp: String? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null,

	@field:SerializedName("expiryDate")
	val expiryDate: String? = null,

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("sendOtpFlag")
	val sendOtpFlag: String? = null,

	@field:SerializedName("accountNo")
	val accountNo: String? = null,

	@field:SerializedName("customerId")
	val customerId: String? = null,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfo? = null,

	@field:SerializedName("partnerReferenceNo")
	val partnerReferenceNo: String? = null,

	@field:SerializedName("identificationNo")
	val identificationNo: String? = null,

	@field:SerializedName("qParamsURL")
	val qParamsURL: String,

	@field:SerializedName("tokenExpiryTime")
	val tokenExpiryTime: String? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: String? = null,

	@field:SerializedName("linkageToken")
	val linkageToken: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class QParams(
	@field:SerializedName("action")
	val action: String? = null
)
