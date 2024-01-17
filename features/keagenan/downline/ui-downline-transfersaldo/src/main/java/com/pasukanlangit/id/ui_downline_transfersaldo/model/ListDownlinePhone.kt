package com.pasukanlangit.id.ui_downline_transfersaldo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListDownlinePhone(
    val img_url: String,
    val name: String,
    val phoneNumber: String
): Parcelable
