package com.pasukanlangit.id.recapitulation.presentation.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateObjectClass(
    val dateStart: String?,
    val dateEnd: String?,
    val label: String
): Parcelable
