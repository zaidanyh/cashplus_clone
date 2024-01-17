package com.pasukanlangit.cashplus.domain.model

data class OmniRequest(
    val uuid: String,
    val dest: String,
    val mlid: String? = null,
    val packageId: String? = null
)
