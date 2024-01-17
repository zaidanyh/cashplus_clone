package com.pasukanlangit.id.domain_downline.model

data class DetailTransferDownlineResponse(
    val date: String,
    val paymentMethod: String,
    val valueRupiah: String,
    val endingBalanceRupiah: String,
    val destinationNumber: String,
    val transtStat: String,
    val downlineName: String
)
