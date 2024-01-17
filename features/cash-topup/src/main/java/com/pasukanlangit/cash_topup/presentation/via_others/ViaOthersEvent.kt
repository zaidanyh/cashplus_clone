package com.pasukanlangit.cash_topup.presentation.via_others

sealed class ViaOthersEvent {
    data class GetCostNicePay(
        val bankMitraCode: String, val payMethod: String, val amount: String
    ): ViaOthersEvent()
    object RemoveGetCostNicePayError: ViaOthersEvent()
}