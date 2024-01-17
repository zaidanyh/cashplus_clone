package com.pasukanlangit.id.ui_cashgold_buy.home

import com.pasukanlangit.id.usecase.ChartDuration

sealed class HomeEvent {
    object GetUserBalance: HomeEvent()
    object GetGoldPrice: HomeEvent()
    object CheckStatusRegister: HomeEvent()
    object RemoveHeadQueue: HomeEvent()
    object CheckKycStatus: HomeEvent()
    data class GetChart(val chartDuration: ChartDuration): HomeEvent()
}
