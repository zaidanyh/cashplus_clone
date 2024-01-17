package com.pasukanlangit.id.cash_transfer.domain.utils

import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionResponse

data class GlobalBankCreateTransTAGResponse(
    val quoteId: String,
    val tagResponse: TransferTransactionResponse
)
