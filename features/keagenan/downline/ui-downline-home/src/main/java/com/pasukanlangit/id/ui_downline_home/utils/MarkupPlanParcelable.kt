package com.pasukanlangit.id.ui_downline_home.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkupPlanParcelable(
    val codeMarkupPlan: String,
    val description: String
): Parcelable
