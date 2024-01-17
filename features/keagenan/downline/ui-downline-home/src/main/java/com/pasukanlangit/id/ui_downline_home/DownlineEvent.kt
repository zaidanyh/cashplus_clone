package com.pasukanlangit.id.ui_downline_home

sealed class DownLineEvent {
    data class SetAllProductMarkup(
        val dest: String,
        val markupValue: String
    ): DownLineEvent()
    data class ResetMarkup(
        val value: String
    ): DownLineEvent()
    data class SetDate(
        val dateStart: String,
        val dateEnd: String
    ): DownLineEvent()
    data class GenerateQR(
        val amount: String
    ): DownLineEvent()

    object RemoveHeadMessageSummary: DownLineEvent()
    object RemoveHeadMessageTrxCount: DownLineEvent()
    object RemoveHeadMessage: DownLineEvent()
}
