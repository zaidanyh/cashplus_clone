package com.pasukanlangit.id.ui_downline_home.priceplan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkupProduct(
    val kodeProduct: String,
    val markup: String,
    val mainMarkup: String
): Parcelable
