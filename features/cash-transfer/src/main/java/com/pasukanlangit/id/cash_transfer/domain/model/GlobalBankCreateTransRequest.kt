package com.pasukanlangit.id.cash_transfer.domain.model

data class GlobalBankCreateTransRequest(
    val uuid: String,
    val quoteId: String,
    val relationCode: String,
    val bankCode: String,
    val bankAccNum: String,
    val countryCode: String,
    val firstName: String,
    val lastName: String,
    val addressStreet: String,
    val addressCity: String,
    val nationCode: String
)
