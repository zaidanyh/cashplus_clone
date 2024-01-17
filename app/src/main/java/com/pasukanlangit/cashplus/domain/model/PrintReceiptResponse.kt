package com.pasukanlangit.cashplus.domain.model

data class PrintReceiptResponse(
    val fee: String,
    val feeCounter: String,
    val price: String,
    val receipt: String
)
