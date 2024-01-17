package com.pasukanlangit.id.cash_transfer.domain.model

data class RateResponse(
    val quoteId: String,
    val rate: Double,
    val usdIdrRate: String,
    val kursMarkup: String,
    val adminTotal: String,
    val amountSend: String,
    val idrAmount: String,
)
