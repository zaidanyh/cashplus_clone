package com.pasukanlangit.id.library_core.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class EKycCheckStatusResponseDto(
    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("msg")
    val message: String,

    @field:SerializedName("ekyc_nik_uploaded")
    val EKycNikUploaded: String,

    @field:SerializedName("ekyc_selfie_uploaded")
    val EKycSelfieUploaded: String,

    @field:SerializedName("ekyc_ocr_status")
    val EKycOcrStatus: String,

    @field:SerializedName("ekyc_ocr_status_text")
    val EKycOcrDescription: String,

    @field:SerializedName("ekyc_selfie_status")
    val EKycSelfieStatus: String,

    @field:SerializedName("ekyc_selfie_status_text")
    val EkycSelfieDescription: String,

    @field:SerializedName("is_kyc_approved")
    val isKycApproved: String,

    @field:SerializedName("is_kyc_rejected")
    val isKycRejected: String,

    @field:SerializedName("reject_note")
    val rejectNote: String?
)
