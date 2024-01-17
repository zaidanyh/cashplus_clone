package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnitPriceResponse(
    val name: String,
    val qty: String,
    val price: String,
    val admin: String,
    val totalPrice: String
): Parcelable
