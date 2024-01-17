package com.pasukanlangit.id.ui_cashgold_withdraw.home

sealed class CashGoldWDEvent {
    object GetGoldBalance: CashGoldWDEvent()
    object GetSt24Balance: CashGoldWDEvent()
    object GetWithDrawProduct: CashGoldWDEvent()
    object RemoveHeadQueue: CashGoldWDEvent()
    object CheckKyc: CashGoldWDEvent()
}
