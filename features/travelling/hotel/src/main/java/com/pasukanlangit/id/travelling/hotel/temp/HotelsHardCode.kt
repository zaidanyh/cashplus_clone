package com.pasukanlangit.id.travelling.hotel.temp

import android.content.Context
import com.pasukanlangit.id.travelling.hotel.R

data class HotelsHardCode(
    val cityName: String,
    val cityCode: String,
    var isActive: Boolean = false
)

fun getLocalCityList(context: Context): List<HotelsHardCode> = listOf(
    HotelsHardCode(
        "Jakarta",
        "8691",
        true
    ),
    HotelsHardCode(
        "Bali",
        "17193"
    ),
    HotelsHardCode(
        "Bandung",
        "18943"
    ),
    HotelsHardCode(
        "Lombok",
        "16842"
    ),
    HotelsHardCode(
        "Surabaya",
        "10779"
    ),
    HotelsHardCode(
        "Yogyakarta",
        "14018"
    ),
    HotelsHardCode(
        context.getString(R.string.other),
        "-1"
    )
)