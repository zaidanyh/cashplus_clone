package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResetPINRequest(
    val uuid: String,
    val newPin: String,
    val via: String
): Parcelable