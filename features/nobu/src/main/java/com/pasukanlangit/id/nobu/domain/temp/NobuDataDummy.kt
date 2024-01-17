package com.pasukanlangit.id.nobu.domain.temp

data class FilteringHistory(
    val name: String,
    val value: String?
)

object NobuDataDummy {
    fun allFilterType(): List<FilteringHistory> {
        return listOf(
            FilteringHistory("Jenis", null),
            FilteringHistory("Topup", "TOPUP"),
            FilteringHistory("Pembelian", "PURCHASE")
        )
    }
}