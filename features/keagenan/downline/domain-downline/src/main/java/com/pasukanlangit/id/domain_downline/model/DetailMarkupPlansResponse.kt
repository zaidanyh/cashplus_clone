package com.pasukanlangit.id.domain_downline.model

data class DetailMarkupPlansResponse(
    val detailMarkupPlan: List<DetailMarkupPlan>?
)

data class DetailMarkupPlan(
    val codeMarkupPlan: String,
    val codeProduct: String,
    val markup: String
)
