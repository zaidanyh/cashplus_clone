package com.pasukanlangit.cashplus.ui.pages.others

import android.content.Context
import com.pasukanlangit.cashplus.R

data class Menu(
    val position: Int,
    val icon: Int,
    val backgroundTint: Int,
    val nameMenu: String
)

object OtherMenus {
    fun getMenus(context: Context): List<Menu> {
        return listOf(
            Menu(
                0, R.drawable.ic_mouse_square,
                R.color.orange_50, context.getString(R.string.keagenan)
            ),
            Menu(
                1, R.drawable.ic_status_product,
                R.color.red_100, context.getString(R.string.status_product)
            ),
            Menu(
                2, R.drawable.ic_qr_code_line,
                R.color.blue_50, context.getString(R.string.qris)
            ),
            Menu(
                3, R.drawable.ic_map_pin_user,
                R.color.teal_100, context.getString(R.string.closest_agent)
            ),
            Menu(
                4, R.drawable.ic_recapitulation,
                R.color.purple_50, context.getString(R.string.recapitulation)
            ),
            Menu(
                5, R.drawable.ic_licence,
                R.color.blue_100, context.getString(R.string.licence)
            ),
            Menu(
                6, R.drawable.ic_about,
                R.color.sky_50, context.getString(R.string.about)
            ),
            Menu(
                7, R.drawable.ic_message_question,
                R.color.cyan_100, context.getString(R.string.faq)
            )
        )
    }
}