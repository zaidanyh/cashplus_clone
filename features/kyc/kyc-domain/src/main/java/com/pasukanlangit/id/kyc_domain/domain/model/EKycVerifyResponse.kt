package com.pasukanlangit.id.kyc_domain.domain.model

data class EKycVerifyResponse(
    val rc: String,
    val msg: String,
    val ocrNIK: String?,
    val ocrFullName: String?,
    val ownerName: String?
)
