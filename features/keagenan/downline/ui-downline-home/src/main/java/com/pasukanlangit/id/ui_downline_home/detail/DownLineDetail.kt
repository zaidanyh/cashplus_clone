package com.pasukanlangit.id.ui_downline_home.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownLineDetail(
    val accountName: String,
    val phoneNumber: String,
    val address: String,
    val balance: String,
    val ownerName: String,
    val trxCount: String,
    val markup: String,
    val downLineCount: String,
    val isActive: Boolean
): Parcelable
