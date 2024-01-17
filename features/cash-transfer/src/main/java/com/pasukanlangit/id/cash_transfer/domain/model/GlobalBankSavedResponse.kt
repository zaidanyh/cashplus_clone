package com.pasukanlangit.id.cash_transfer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GlobalBankSavedResponse(
    val bankCode: String,
    val countryCode: String,
    val relationship: String,
    val nationCode: String,
    val address: String,
    val city: String,
    val currency: String,
    val currencyDsc: String,
    val countryName: String,
    val bankName: String,
    val bankAccNum: String,
    val bankAccName: String,
    val imgUrl: String?,
    val countryImgUrl: String?
): Parcelable
