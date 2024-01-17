package com.pasukanlangit.id.ui_downline_home.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryAddProduct(
    val dataSummary: List<SummaryProduct>
): Parcelable

@Parcelize
data class SummaryProduct(
    val codeProduct: String,
    var markup: String,
    val category: String,
    val positionIndex: Int?
): Parcelable
