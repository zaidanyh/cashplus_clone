package com.pasukanlangit.id.model

data class Address(
    val provinsi: String,
    val keterangan: String,
    val kecamatan: String,
    val kodepos: String,
    val id: Int,
    val isDefault: Boolean,
    val kabupaten: String,
    val kelurahan: String,
    val alamat: String
)
