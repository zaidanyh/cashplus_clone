package com.pasukanlangit.cash_topup.presentation.via_others.qris

sealed class ViaQRISEvent {
    data class TopUpViaQRIS(val amount: String): ViaQRISEvent()
    object RemoveTopUpViaQRISError: ViaQRISEvent()
}
