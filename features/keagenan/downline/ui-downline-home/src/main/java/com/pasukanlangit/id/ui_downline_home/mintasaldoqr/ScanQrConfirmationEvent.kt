package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

sealed class ScanQrConfirmationEvent {
    data class SetIdQr(val id: String): ScanQrConfirmationEvent()
    data class OnSubmitPin(val pin: String): ScanQrConfirmationEvent()
    object RemoveHeadMessage: ScanQrConfirmationEvent()
}
