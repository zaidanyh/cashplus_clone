package com.pasukanlangit.id.kyc_presentation.pages.init

sealed class EKycEvents {
    object RemoveHeadMessage: EKycEvents()
    object CheckEKycStatus: EKycEvents()
}