package com.pasukanlangit.id.nobu.domain.model

data class AccountBindingRequest(
    val uuid: String,
    val phone: String,
    val email: String,
    val name: String,
    val redirectUrl: String
)
