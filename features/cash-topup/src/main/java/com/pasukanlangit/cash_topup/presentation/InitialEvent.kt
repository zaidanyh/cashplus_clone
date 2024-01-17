package com.pasukanlangit.cash_topup.presentation

sealed class InitialEvent {
    object GetBalance: InitialEvent()
    object RemoveBalanceError: InitialEvent()
    object GetFlipAcceptList: InitialEvent()
    object RemoveHeadMessage: InitialEvent()
}
