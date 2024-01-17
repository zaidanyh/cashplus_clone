package com.pasukanlangit.id.ui_downline_home.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkupProductResponseParcel(
    val dataProduct: List<DataProductMarkup>
): Parcelable

@Parcelize
data class DataProductMarkup(
    val kodeProduct: String,
    val kodeProvider: String,
    val category: String,
    val price: String,
    val outlet_price: String,
    val isActive: String,
    val markup: String,
    val mainMarkup: String
): Parcelable