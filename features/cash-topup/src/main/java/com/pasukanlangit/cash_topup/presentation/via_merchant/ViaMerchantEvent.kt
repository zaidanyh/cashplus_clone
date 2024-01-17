package com.pasukanlangit.cash_topup.presentation.via_merchant

sealed class ViaMerchantEvent {
    data class TopUpViaMerchant(
        val bankMitraCode: String, val payMethod: String,
        val amount: String
    ): ViaMerchantEvent()
    object RemoveViaMerchantError: ViaMerchantEvent()
}
