package com.pasukanlangit.id.domain_downline.model

data class MarkupProductResponse(
    val kodeProduct: String,
    val kodeProvider: String,
    val category: String,
    val price: String,
    val outlet_price: String,
    val isActive: String,
    var markup: String,
    val mainMarkup: String
)
