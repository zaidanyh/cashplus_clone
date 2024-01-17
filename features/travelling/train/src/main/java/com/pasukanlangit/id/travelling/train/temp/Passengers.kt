package com.pasukanlangit.id.travelling.train.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passengers(
    val adult: Int = 1,
    val infant: Int = 0
): Parcelable
