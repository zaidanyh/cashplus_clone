package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    val uuid: String,
    val phone: String,
    var via: String
): Parcelable
