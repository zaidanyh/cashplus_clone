package com.pasukanlangit.id.domain_downline.model

data class DetailMarkupPlanRequest(
    val uuid: String,
    val codeMarkupPlan: String,
    val downLinePhone: String? = null
)
