package com.pasukanlangit.cash_topup.presentation.via_va

sealed class ViaVirtualAccountEvent {
    data class TopUpViaVA(
        val bankCode: String,
        val amount: String,
        val paymentMethod: String? = null,
        val isFlipAccept: Boolean = false
    ): ViaVirtualAccountEvent()
    object RemoveViaVAError: ViaVirtualAccountEvent()
}