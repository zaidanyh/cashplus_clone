package com.pasukanlangit.id.travelling.hotel.find.util

import android.content.Context
import com.pasukanlangit.id.travelling.hotel.R

data class Facilities(
    val icon: Int,
    val value: String
)

object FacilitiesUtils {
    fun getFacilities(context: Context): List<Facilities> {
        return listOf(
            Facilities(
                R.drawable.ic_room_only,
                context.getString(R.string.room_only)
            ),
            Facilities(
                R.drawable.ic_breakfast,
                context.getString(R.string.breakfast)
            ),
            Facilities(
                R.drawable.ic_breakfast,
                context.getString(R.string.breakfast_more_people)
            ),
            Facilities(
                R.drawable.ic_parking,
                context.getString(R.string.parking)
            ),
            Facilities(
                R.drawable.ic_coffee_tea,
                context.getString(R.string.coffee_tea)
            ),
            Facilities(
                R.drawable.ic_express_check_in,
                context.getString(R.string.express_check_in)
            ),
            Facilities(
                R.drawable.ic_free_wifi,
                context.getString(R.string.free_wifi)
            ),
            Facilities(
                R.drawable.ic_fitness_access,
                context.getString(R.string.free_fitness_access)
            ),
            Facilities(
                R.drawable.ic_pool_access,
                context.getString(R.string.free_pool_access)
            )
        )
    }
}