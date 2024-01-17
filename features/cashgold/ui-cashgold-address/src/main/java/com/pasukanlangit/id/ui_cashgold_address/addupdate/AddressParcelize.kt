package com.pasukanlangit.id.ui_cashgold_address.addupdate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressParcelize(
    val provinsi: String,
    val kecamatan: String,
    val kodepos: String,
    val id: Int,
    val isDefault: Boolean,
    val kabupaten: String,
    val kelurahan: String,
    val alamat: String
): Parcelable
