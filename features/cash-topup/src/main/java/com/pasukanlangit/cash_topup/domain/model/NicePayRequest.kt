package com.pasukanlangit.cash_topup.domain.model

data class NicePayRequest(
    val uuid: String,
    val bankMitraCode: String,
    val payMethod: String,
    val amt: String? = null,
    val amount: String? = null,
    val billingPhone: String? = null
)
