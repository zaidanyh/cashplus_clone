package com.pasukanlangit.id.kyc_domain.domain.usecase

data class KycUseCase(
    val eKycProfile: EKycProfile,
    val eKycStatus: EKycStatusUseCase,
    val eKycUploadVerify: EKycUploadVerifyUseCase,
    val eKycOnlyVerify: EKycVerifyUseCase
)
