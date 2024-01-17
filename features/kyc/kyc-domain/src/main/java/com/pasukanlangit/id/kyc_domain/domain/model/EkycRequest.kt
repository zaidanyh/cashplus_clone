package com.pasukanlangit.id.kyc_domain.domain.model

data class EkycRequest(
    val uuid: String,
    val uploadType: String,
    val base64: String
)
