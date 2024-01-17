package com.pasukanlangit.cash_topup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpViaVAResult(
    val adminCost: String,
    val vaNumber: String
): Parcelable
