package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class AccountBindingResponseDTO(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("userInfo")
	val userInfo: UserInfo? = null,

	@field:SerializedName("accessTokenInfo")
	val accessTokenInfo: AccessTokenInfo? = null,

	@field:SerializedName("redirectUrl")
	val redirectUrl: String? = null,

	@field:SerializedName("referenceNo")
	val referenceNo: String? = null,

	@field:SerializedName("nextAction")
	val nextAction: String? = null,

	@field:SerializedName("params")
	val params: Params? = null,

	@field:SerializedName("responseCode")
	val responseCode: String? = null,

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("additionalInfo")
	val additionalInfo: AdditionalInfoBinding? = null,

	@field:SerializedName("partnerReferenceNo")
	val partnerReferenceNo: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("responseMessage")
	val responseMessage: String? = null,

	@field:SerializedName("linkageToken")
	val linkageToken: String? = null
)

data class AccessTokenInfo(

	@field:SerializedName("expiresIn")
	val expiresIn: String? = null,

	@field:SerializedName("tokenStatus")
	val tokenStatus: String? = null,

	@field:SerializedName("reExpiresIn")
	val reExpiresIn: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)

data class AdditionalInfoBinding(

	@field:SerializedName("name")
	val name: String? = null
)

data class Params(

	@field:SerializedName("pinWebViewUrl")
	val pinWebViewUrl: String? = null,

	@field:SerializedName("redirectToDeeplink")
	val redirectToDeeplink: String? = null,

	@field:SerializedName("action")
	val action: String? = null
)

data class UserInfo(

	@field:SerializedName("publicUserId")
	val publicUserId: String? = null
)
