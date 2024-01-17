package com.pasukanlangit.id.travelling.hotel.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CitySelected(
    val cityName: String,
    val cityCode: String
): Parcelable
