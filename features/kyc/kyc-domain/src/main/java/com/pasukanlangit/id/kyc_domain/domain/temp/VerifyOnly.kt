package com.pasukanlangit.id.kyc_domain.domain.temp

data class VerifyOnly(
    val rc: String,
    val msg: String,
    val ocrNIK: String?,
    val ocrFullName: String?,
    val ownerName: String?,
    val verificationType: String?
)