package com.pasukanlangit.cash_topup.presentation.via_ewallet

sealed class ViaEWalletEvent {
    data class SetAmountAndAdminCost(
        val amount: String, val adminCost: String, val billingPhone: String
        ): ViaEWalletEvent()
    data class GetCostNicePay(
        val bankMitraCode: String, val payMethod: String, val amount: String
    ): ViaEWalletEvent()
    data class TopUpViaEWallet(
        val bankMitraCode: String, val payMethod: String, val amount: String, val billingPhone: String
    ): ViaEWalletEvent()
    object RemoveGetCostNicePayError: ViaEWalletEvent()
    object RemoveTopUpViaEWalletError: ViaEWalletEvent()
}