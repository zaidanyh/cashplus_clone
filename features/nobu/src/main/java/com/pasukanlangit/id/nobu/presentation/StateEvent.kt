package com.pasukanlangit.id.nobu.presentation

sealed class StateEvent {
    object CheckBinding: StateEvent()
    object UnBindAccount: StateEvent()
    object FetchProfile: StateEvent()
    data class SetDataBinding(
        val phone: String,
        val email: String,
        val name: String
    ): StateEvent()
    data class AccountBinding(
        val phone: String,
        val email: String,
        val name: String
    ): StateEvent()
    data class VerifyOtp(val otpCode: String): StateEvent()
    object RemoveHeadMessage: StateEvent()
    object RemoveBindingHeadMessage: StateEvent()
}
