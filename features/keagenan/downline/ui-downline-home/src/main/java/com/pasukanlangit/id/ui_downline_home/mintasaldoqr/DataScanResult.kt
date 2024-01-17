package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DataScanResult(
	val amount: Int,
	val full_name: String,
	val id: String,
	val dest: String
) : Parcelable
