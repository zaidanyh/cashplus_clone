package com.pasukanlangit.id.cash_transfer.domain.model

data class RateRequest(
    val uuid: String,
    val currency: String,
    val amount: String
)
