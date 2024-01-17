package com.pasukanlangit.id.travelling.flight.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passengers(
    val adult: Int = 1,
    val child: Int = 0,
    val infant: Int = 0
): Parcelable
