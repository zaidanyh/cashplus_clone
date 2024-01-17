package com.pasukanlangit.cash_topup.domain.model

data class FlipAcceptRequest(
    val uuid: String,
    val bankCode: String,
    val amount: String
)
