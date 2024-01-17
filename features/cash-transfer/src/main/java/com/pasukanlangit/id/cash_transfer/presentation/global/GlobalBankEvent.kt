package com.pasukanlangit.id.cash_transfer.presentation.global

sealed class GlobalBankEvent {
    object GlobalBankSaved: GlobalBankEvent()
    object RemoveGlobalBankError: GlobalBankEvent()
    data class DeleteGlobalBankSaved(
        val bankCode: String,
        val bankAccNum: String
    ): GlobalBankEvent()
    object RemoveDeleteGlobalBankSaved: GlobalBankEvent()
}
