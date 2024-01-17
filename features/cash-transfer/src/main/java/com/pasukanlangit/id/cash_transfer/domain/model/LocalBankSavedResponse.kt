package com.pasukanlangit.id.cash_transfer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalBankSavedResponse(
    val bank_code: String,
    val bank_name: String,
    val bank_acc_num: String,
    val bank_acc_name: String,
    val bank_img: String
): Parcelable

@Parcelize
class LocalBankSaved: ArrayList<LocalBankSavedResponse>(), Parcelable