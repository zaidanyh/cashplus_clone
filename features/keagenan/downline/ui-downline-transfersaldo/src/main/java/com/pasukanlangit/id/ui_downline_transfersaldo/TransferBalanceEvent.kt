package com.pasukanlangit.id.ui_downline_transfersaldo

sealed class TransferBalanceEvent {
    data class TransferBalanceDownLine(
        val pin: String,
        val dest: String?,
        val nominal: String?
    ): TransferBalanceEvent()
    object RemoveHeadQueue: TransferBalanceEvent()
    object CheckBalance: TransferBalanceEvent()
    object RemoveCheckBalanceError: TransferBalanceEvent()
}