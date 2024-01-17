package com.pasukanlangit.id.library_core.domain.model

data class EKycCheckStatus(
    val eKycOcrUploaded: Boolean,
    val eKycSelfieUploaded: Boolean,
    val eKycOcrUploadedStatus: Boolean,
    val eKycSelfieUploadedStatus: Boolean,
    val isKycApproved: Boolean,
    val isKycRejected: Boolean,
    val rejectNote: String?
)
