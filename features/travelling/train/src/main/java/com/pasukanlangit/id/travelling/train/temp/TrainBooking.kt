package com.pasukanlangit.id.travelling.train.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainBooking(
    val session: String,
    val trainCode: String,
    val trainClass: String,
    val trainSubClass: String
): Parcelable
