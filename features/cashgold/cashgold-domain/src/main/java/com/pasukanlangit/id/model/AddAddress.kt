package com.pasukanlangit.id.model

data class AddAddress(
    val zipCode: String,
    val address: String,
    val province: String,
    val city: String,
    val district: String,
    val village: String,
    val uuid: String
)
