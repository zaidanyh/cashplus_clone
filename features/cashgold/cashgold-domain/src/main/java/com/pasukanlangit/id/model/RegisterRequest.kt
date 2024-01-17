package com.pasukanlangit.id.model

data class RegisterRequest(
    val phone: String,
    val name: String,
    val email: String,
    val uuid: String,
)