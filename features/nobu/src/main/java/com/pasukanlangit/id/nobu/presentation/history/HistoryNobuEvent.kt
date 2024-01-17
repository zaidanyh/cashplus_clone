package com.pasukanlangit.id.nobu.presentation.history

sealed class HistoryNobuEvent {
    data class FilterByDateHistory(
        val dateStart: String? = null,
        val dateEnd: String? = null
    ): HistoryNobuEvent()
    object RemoveHeadMessage: HistoryNobuEvent()
}