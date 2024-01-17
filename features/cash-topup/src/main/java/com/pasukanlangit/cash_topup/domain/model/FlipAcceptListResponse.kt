package com.pasukanlangit.cash_topup.domain.model

data class FlipAcceptListResponse(
    val bankCode: String,
    val flipBankCode: String,
    val bankName: String,
    val imgUrl: String?,
    val cost: String,
    val priority: String
)
