package com.pasukanlangit.id.cash_transfer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalBankListResponse(
    val bankCode: String,
    val bankName: String,
    val biFastCode: String,
    val imgBank: String
): Parcelable
