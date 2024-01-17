package com.pasukanlangit.cashplus.domain.model

data class OmniMenuResponse(
    val menuTitle: List<MenuTitle>?,
    val error: String?
)

data class MenuTitle(
    val icon: String,
    val mlid: String,
    val title: MenuLanguage
)

data class MenuLanguage(
    val en: String,
    val id: String
)