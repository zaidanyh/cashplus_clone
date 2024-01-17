package com.pasukanlangit.id.ui_cashgold_withdraw.tag

import com.pasukanlangit.id.usecase.TrxType

sealed class TagWithDrawEvent {
    object GetMainAddress: TagWithDrawEvent()
    object CheckIsKtpIsEmpty: TagWithDrawEvent()
    object RemoveHeadMessageQueue: TagWithDrawEvent()
    data class SendTrxGold(val type: TrxType, val pin: String): TagWithDrawEvent()
    data class SetWithDrawProduct(val withDrawProduct: ProductWithDraw): TagWithDrawEvent()
    object WithDrawBook: TagWithDrawEvent()
}