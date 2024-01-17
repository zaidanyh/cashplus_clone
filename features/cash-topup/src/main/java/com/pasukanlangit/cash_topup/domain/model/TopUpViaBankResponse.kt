package com.pasukanlangit.cash_topup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpViaBankResponse(
    val amount: String,
    val uniqueId: String,
    val bankName: String,
    val bankAccNum: String,
    val bankAccName: String
): Parcelable
