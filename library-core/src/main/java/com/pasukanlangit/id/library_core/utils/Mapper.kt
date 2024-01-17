package com.pasukanlangit.id.library_core.utils

import com.pasukanlangit.id.library_core.datasource.network.dto.CheckKycResponseDto
import com.pasukanlangit.id.library_core.datasource.network.dto.EKycCheckStatusResponseDto
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import com.pasukanlangit.id.library_core.domain.model.EKycCheckStatus
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest

fun UUIDRequest.toUUIDDto() =
    UUIDDtoRequest(uuid = uuid)

fun CheckKycResponseDto.toKycStatusDomain(): KycStatus =
    KycStatus(
        isIdentityCardUploaded = this.identitycard == "1",
        isIdentityWithSelfieUploaded = this.selfieidentitycard == "1",
        isTaxIdUploaded  = this.taxidnumber == "1",
        isApproved = this.isKycApproved == "1",
        isRejected = this.isKycRejected == "1",
        rejectedMessage = this.rejectNote
    )

fun EKycCheckStatusResponseDto.toEKycCheckStatus(): EKycCheckStatus =
    EKycCheckStatus(
        eKycOcrUploaded = this.EKycNikUploaded == "1",
        eKycSelfieUploaded = this.EKycSelfieUploaded == "1",
        eKycOcrUploadedStatus = this.EKycOcrStatus == "1",
        eKycSelfieUploadedStatus = this.EKycSelfieStatus == "1",
        isKycApproved = this.isKycApproved == "1",
        isKycRejected = this.isKycRejected == "1",
        rejectNote = this.rejectNote
    )

