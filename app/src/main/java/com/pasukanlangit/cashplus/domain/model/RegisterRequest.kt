package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterRequest(
    val uuid: String,
    val phoneNumber: String,
    val fullName: String,
    val email: String,
    val referral: String,
    val ownerName: String,
    val via: String
): Parcelable
