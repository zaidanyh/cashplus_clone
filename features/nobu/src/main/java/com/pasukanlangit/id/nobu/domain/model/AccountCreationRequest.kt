package com.pasukanlangit.id.nobu.domain.model

data class AccountCreationRequest(
    val uuid: String,
    val phone: String,
    val email: String,
    val name: String,
    val securityAnswer: String?,
    val securityQuestion: String?,
    val securityCode: String?,
    val redirectUrl: String?
)
