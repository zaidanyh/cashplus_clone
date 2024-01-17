package com.pasukanlangit.id.travelling.ship.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShipSchedules(
    val name: String,
    val number: String,
    val code: String,
    val shipClass: String,
    val basicFare: Int,
    val admin: Int,
    val price: Int,
    val maleSeat: String,
    val femaleSeat: String
): Parcelable
