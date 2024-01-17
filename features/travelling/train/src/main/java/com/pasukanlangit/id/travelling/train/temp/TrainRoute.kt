package com.pasukanlangit.id.travelling.train.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainRoute(
    val code: String,
    val location: String
):Parcelable
