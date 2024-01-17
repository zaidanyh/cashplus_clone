package com.pasukanlangit.id.cash_transfer.domain.model

data class SavingGlobalBankRequest(
    val uuid: String,
    val bankCode: String,
    val bankAccNum: String,
    val bankAccName: String,
    val relationCode: String,
    val nationCode: String,
    val addressStreet: String,
    val addressCity: String
)
