package com.pasukanlangit.id.nobu.domain.model

data class AccountBindingResponse(
    val msg: String?,
    val userInfo: UserInfoResponse?,
    val accessTokenInfo: AccessTokenResponse?,
    val redirectUrl: String?,
    val referenceNo: String?,
    val nextAction: String?,
    val params: ParamsResponse?,
    val responseCode: String?,
    val rc: String?,
    val additionalInfo: AdditionalInfoResponse?,
    val partnerReferenceNo: String?,
    val id: String?,
    val responseMessage: String?,
    val linkageToken: String?
)

data class AccessTokenResponse(
    val expiresIn: String?,
    val tokenStatus: String?,
    val reExpiresIn: String?,
    val accessToken: String?,
    val refreshToken: String?
)

data class AdditionalInfoResponse(
    val name: String?
)

data class ParamsResponse(
    val pinWebViewUrl: String?,
    val redirectToDeeplink: String?,
    val action: String?
)

data class UserInfoResponse(
    val publicUserId: String?
)
