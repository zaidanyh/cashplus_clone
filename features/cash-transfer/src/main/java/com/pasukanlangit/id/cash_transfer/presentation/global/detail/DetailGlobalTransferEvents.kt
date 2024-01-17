package com.pasukanlangit.id.cash_transfer.presentation.global.detail

sealed class DetailGlobalTransferEvents {
    data class GetRateConversion(val currency: String, val amount: String): DetailGlobalTransferEvents()
    object RemoveHeadMessageError: DetailGlobalTransferEvents()

    object CheckBalance: DetailGlobalTransferEvents()
    object RemoveBalanceError: DetailGlobalTransferEvents()

    data class GlobalBankCreateTrans(
        val quoteId: String, val relationCode: String, val nationCode: String, val bankCode: String,
        val bankAccNum: String, val bankAccName: String, val countryCode: String, val address: String,
        val city: String, val reqId: String?
    ): DetailGlobalTransferEvents()
    object RemoveCreateTransError: DetailGlobalTransferEvents()

    data class TransferTransaction(
        val productCode: String, val dest: String, val pin: String, val reqId: String?
    ): DetailGlobalTransferEvents()
    object RemoveTransferTransError: DetailGlobalTransferEvents()
}