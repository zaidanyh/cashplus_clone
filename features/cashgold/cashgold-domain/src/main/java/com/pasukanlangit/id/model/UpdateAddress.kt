package com.pasukanlangit.id.model

data class UpdateAddress(
    val zipCode: String,
    val address: String,
    val province: String,
    val city: String,
    val district: String,
    val village: String,
    val uuid: String,
    val isMain: Boolean,
    val id: Int
)
