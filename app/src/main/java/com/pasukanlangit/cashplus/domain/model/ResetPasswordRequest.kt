package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResetPasswordRequest(
    val uuid: String,
    val phoneNumber: String,
    val newPassword: String,
    val via: String
): Parcelable
