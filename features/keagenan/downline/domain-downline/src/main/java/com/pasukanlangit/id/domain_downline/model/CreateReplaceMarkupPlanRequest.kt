package com.pasukanlangit.id.domain_downline.model

data class CreateReplaceMarkupPlanRequest(
    val uuid: String,
    val markupCode: String,
    val description: String,
    val data: List<ProductMarkup>
)

data class ProductMarkup(
    val productCode: String,
    val markup: String
)
