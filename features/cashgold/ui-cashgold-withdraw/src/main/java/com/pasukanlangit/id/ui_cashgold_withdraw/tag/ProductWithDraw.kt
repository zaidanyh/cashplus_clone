package com.pasukanlangit.id.ui_cashgold_withdraw.tag

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductWithDraw(
    val providerId: String,
    val productDomination: String,
    val productDominationRaw: String,
    val fee: String,
    val feeRaw: Long,
    val qty: String
): Parcelable
