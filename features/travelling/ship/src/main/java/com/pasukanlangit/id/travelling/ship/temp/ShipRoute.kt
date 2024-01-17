package com.pasukanlangit.id.travelling.ship.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShipRoute(
    val code: String,
    val name: String
): Parcelable
