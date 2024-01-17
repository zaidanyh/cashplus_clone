package com.pasukanlangit.cash_topup.presentation.via_bank

sealed class ViaBankEvent {
    data class TopUpViaBank(
        val bank: String,
        val value: String
    ): ViaBankEvent()
    object RemoveViaBankError: ViaBankEvent()
}
