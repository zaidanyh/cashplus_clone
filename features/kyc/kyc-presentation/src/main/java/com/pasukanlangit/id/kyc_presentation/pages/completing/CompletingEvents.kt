package com.pasukanlangit.id.kyc_presentation.pages.completing


sealed class CompletingEvents {
    data class SetStateFirst(val state: Boolean): CompletingEvents()
    data class SetStateSecond(val state: Boolean): CompletingEvents()
    object RemoveHeadProfileMessage: CompletingEvents()
    object EKycProfileUser: CompletingEvents()
    object RemoveHeadUploadMessage: CompletingEvents()
    data class EKycUploadVerify(
        val verificationType: String,
        val uploadType: String,
        val base64Data: String
    ): CompletingEvents()
    object RemoveHeadVerifyMessage: CompletingEvents()
    data class EKycOnlyVerify(
        val verificationType: String
    ): CompletingEvents()
}
