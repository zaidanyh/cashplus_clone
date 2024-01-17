package com.pasukanlangit.cash_topup.domain.model

data class TopUpViaBankRequest(
    val uuid: String,
    val bankName: String,
    val value: String
)
