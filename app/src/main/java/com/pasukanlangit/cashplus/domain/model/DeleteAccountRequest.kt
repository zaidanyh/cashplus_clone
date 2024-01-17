package com.pasukanlangit.cashplus.domain.model

data class DeleteAccountRequest(
    val uuid: String,
    val reason: String
)
