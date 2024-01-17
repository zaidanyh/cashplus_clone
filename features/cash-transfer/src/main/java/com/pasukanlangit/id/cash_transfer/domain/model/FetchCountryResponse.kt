package com.pasukanlangit.id.cash_transfer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchCountryResponse(
    val codeCountry: String,
    val nameCountry: String,
    val currency: String,
    val currencyDsc: String,
    val imgUrl: String?
): Parcelable
