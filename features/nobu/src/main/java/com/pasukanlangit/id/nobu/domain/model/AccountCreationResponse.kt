package com.pasukanlangit.id.nobu.domain.model

data class AccountCreationResponse(
    val referenceNo: String,
    val partnerReferenceNp: String,
    val authCode: String,
    val apiKey: String,
    val accountId: String,
    val state: String,
    val additionalInfo: Additional
)

data class Additional(
    val referralCode: String,
    val securityAnswer: String,
    val securityQuestion: String,
    val securityCode: String
)
