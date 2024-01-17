package com.pasukanlangit.id.travelling.flight.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlightRoute(
    val code: String,
    val city: String,
    val airport: String
): Parcelable
