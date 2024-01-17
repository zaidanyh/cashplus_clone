package com.pasukanlangit.id.domain_downline.model

data class SummaryDownlineResponse(
    val fullname: String,
    val myBalance: String?,
    val myTrxCount: String,
    val downlineCount: String,
    val downlineTotalBalance: String?
)
