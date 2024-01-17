package com.pasukanlangit.id.library_core.domain.model

data class KycStatus(
    val isIdentityCardUploaded: Boolean,
    val isIdentityWithSelfieUploaded: Boolean,
    val isTaxIdUploaded: Boolean,
    val isRejected: Boolean,
    val isApproved: Boolean,
    val rejectedMessage: String
)
