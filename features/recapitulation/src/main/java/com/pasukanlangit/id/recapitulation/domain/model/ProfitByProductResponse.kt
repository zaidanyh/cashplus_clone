package com.pasukanlangit.id.recapitulation.domain.model

data class ProfitByProductResponse(
    val productCode: String,
    val qty: String,
    val modal: String,
    val selling: String,
    val profit: String,
    val desc: String,
    val shortDesc: String,
    val operator: String,
    val category: String,
    val providerCode: String
)