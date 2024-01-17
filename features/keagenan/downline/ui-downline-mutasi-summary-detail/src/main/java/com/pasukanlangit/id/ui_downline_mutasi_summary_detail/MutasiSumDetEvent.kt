package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import com.pasukanlangit.id.core.presentation.MutationSumDetPageType

sealed class MutasiSumDetEvent {
    data class SetPhoneNumberDownLine(
        val phoneNumberDownLine: String
    ): MutasiSumDetEvent()

    data class SetDate(
        val dateStart: String,
        val dateEnd: String,
    ): MutasiSumDetEvent()

    data class SetPageType(
        val mutationSumDetPageType: MutationSumDetPageType
    ): MutasiSumDetEvent()

    data class SetPageFrom(val isFromSubDownLine: Boolean = false): MutasiSumDetEvent()
    object OnSubmit: MutasiSumDetEvent()
    object RemoveHeadMessageQueue: MutasiSumDetEvent()
}
