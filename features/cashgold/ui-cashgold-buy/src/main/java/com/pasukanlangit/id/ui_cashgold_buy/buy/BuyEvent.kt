package com.pasukanlangit.id.ui_cashgold_buy.buy

import com.pasukanlangit.id.model.LockGoldType
import com.pasukanlangit.id.usecase.TrxType

sealed class BuyEvent {
    object GetGoldBalance: BuyEvent()
    object GetGoldPrice: BuyEvent()
    object GetSt24Balance: BuyEvent()
//    object SwapInput: BuyEvent()
    data class SetLockGoldType(val type: LockGoldType): BuyEvent()
    object CheckKyc: BuyEvent()
    data class LockGold(val nominal: String?) : BuyEvent()
    object RemoveHeadQueue: BuyEvent()
    data class SendTrxGold(val type: TrxType, val pin: String): BuyEvent()
}
