package com.pasukanlangit.id.kyc_domain.domain.temp

data class UploadAndVerify(
    val rc: String,
    val msg: String,
    val uploadType: String?,
    val ocrNIK: String?,
    val ocrFullName: String?,
    val ownerName: String?
)
