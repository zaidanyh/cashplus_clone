package com.pasukanlangit.id.travelling.ship.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShipPassengers(
    val male: Int = 1,
    val female: Int = 0,
    val infant: Int = 0
): Parcelable
