package com.pasukanlangit.id.travelling.domain.model

data class TransactionRequest(
    val uuid: String,
    val productCode: String,
    val destination: String,
    val pin: String
)
