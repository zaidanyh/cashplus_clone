package com.pasukanlangit.id.model

data class WithDrawBookRequest(
    val quantity: String,
    val productId: String,
    val uuid: String,
    val addressId: String,
    val denomination: String
)
