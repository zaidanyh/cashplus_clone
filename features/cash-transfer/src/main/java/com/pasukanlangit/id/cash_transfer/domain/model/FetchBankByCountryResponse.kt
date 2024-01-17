package com.pasukanlangit.id.cash_transfer.domain.model

data class FetchBankByCountryResponse(
    val bankCode: String,
    val codeCountry: String,
    val codeSwift: String,
    val bankName: String,
    val imgUrl: String? = ""
)
