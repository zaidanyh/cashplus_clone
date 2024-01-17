package com.pasukanlangit.id.cash_transfer.domain.model

data class FetchCountryRequest(
    val uuid: String,
    val country_name: String = "",
    val currency_dsc: String = ""
)
