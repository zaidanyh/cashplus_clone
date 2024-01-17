package com.pasukanlangit.cash_topup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlipAcceptCreateBillResponse(
    val billLinkId: String,
    val bankCode: String,
    val amount: String,
    val cost: String,
    val vaNumber: String,
    val depositAmount: String
): Parcelable
