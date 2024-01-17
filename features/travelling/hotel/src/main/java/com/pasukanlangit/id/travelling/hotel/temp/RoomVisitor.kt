package com.pasukanlangit.id.travelling.hotel.temp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomVisitor(
    val room: Int,
    val visitor: Int,
    val child: Int
): Parcelable
