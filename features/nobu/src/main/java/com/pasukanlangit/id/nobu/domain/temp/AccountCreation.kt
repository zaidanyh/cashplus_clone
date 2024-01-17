package com.pasukanlangit.id.nobu.domain.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountCreation(
    val phone: String,
    val email: String,
    val name: String,
    val securityAnswer: String? = null,
    val securityQuestion: String? = null,
    val securityCode: String? = null
): Parcelable
