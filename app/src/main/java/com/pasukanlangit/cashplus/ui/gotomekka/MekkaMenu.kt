package com.pasukanlangit.cashplus.ui.gotomekka

data class MekkaMenu (
    val name: String,
    var isActive: Boolean = false
)

fun getMekkaMenu() : List<MekkaMenu> {
    return listOf(
        MekkaMenu(
            "Umroh",
            true
        ),
        MekkaMenu("Umroh Plus"),
        MekkaMenu("Haji Plus"),
        MekkaMenu("Wisata Plus")
    )
}