package com.pasukanlangit.id.cash_transfer.presentation.local.transfer

sealed class TransferStateEvent {
    object CheckBalance: TransferStateEvent()
    data class BankTransferTransaction(
        val codeProduct: String, val dest: String, val pin: String, val reqId: String?
    ): TransferStateEvent()
    object RemoveTransferErrorMessage: TransferStateEvent()
    object RemoveMessageBalance: TransferStateEvent()
}
