package com.pasukanlangit.id.cash_transfer.presentation.local

import com.pasukanlangit.id.cash_transfer.presentation.local.utils.AccountBank

sealed class LocalBankEvent {
    object GetBankSaved: LocalBankEvent()
    object RemoveBankSavedError: LocalBankEvent()
    object GetBankList: LocalBankEvent()
    object RemoveBankListError: LocalBankEvent()
    data class SetAccountBank(val account: AccountBank?): LocalBankEvent()
    object ResetAccountBank: LocalBankEvent()
    data class SendTAGBank(
        val codeProduct: String, val dest: String, val pin: String
    ): LocalBankEvent()
    object RemoveTAGBankError: LocalBankEvent()
    data class SavingBankAcc(
        val bankCode: String, val bankAccNum: String, val bankAccName: String
    ): LocalBankEvent()
    object RemoveSavingBankError: LocalBankEvent()
    data class DeleteBankAcc(
        val bankCode: String, val bankAccNum: String
    ): LocalBankEvent()
    object RemoveDeleteBankError: LocalBankEvent()
}
