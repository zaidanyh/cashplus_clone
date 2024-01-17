package com.pasukanlangit.id.domain_downline.model

data class MarkupPlansResponse(
    val markupPlans: List<MarkupPlan>?
)
data class MarkupPlan(
    val codeMarkupPlan: String,
    val description: String
)
