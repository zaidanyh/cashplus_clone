package com.pasukanlangit.id.kyc_presentation.pages.init

sealed class EKycStatus {
    object None: EKycStatus()
    object IdCardUploaded: EKycStatus()
    object UploadSelfie: EKycStatus()
    object SelfieUploaded: EKycStatus()
    object Waiting: EKycStatus()
    object Approved: EKycStatus()
    data class Rejected(val note: String): EKycStatus()
}
