package com.pasukanlangit.cashplus.domain.model

data class UpdateSellingPriceRequest(
    val uuid: String,
    val productCode: String,
    val newPrice: String
)