package com.pasukanlangit.id.recapitulation.presentation.profit

import kotlinx.coroutines.flow.MutableStateFlow

sealed class RecapProfitEvents {
    data class AllRecapProfit(
        val dateStart: String,
        val dateEnd: String
    ): RecapProfitEvents()
    object RemoveAllRecapProfitMessage: RecapProfitEvents()
    data class SearchRecap(val keyword: MutableStateFlow<String>): RecapProfitEvents()
}
