package com.pasukanlangit.id.cash_transfer.domain.model

data class TransferTransactionRequest(
    val uuid: String,
    val codeProduct: String,
    val dest: String,
    val pin: String,
    val reqId: String?
)
