package com.pasukanlangit.id.kyc_domain.domain.model

data class EkycResponse(
    val uploadType: String,
    val idType: String?,
    val fileExt: String?
)