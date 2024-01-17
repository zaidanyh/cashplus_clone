package com.pasukanlangit.id.model

data class TrxGoldResponse(
    val priceTotalCurrency: String,
    val fee: String,
    val priceRupiah: String,
    val feeCurrency: String,
    val admin: String,
    val gramBuy: String,
    val qty: String,
    val name: String,
    val withdrawData: WithDrawData?,
    val trxGoldData: TrxGoldData?
)

data class TrxGoldData(
    val discountFee: String,
    val pph22FeeRupiah: String,
    val ppn11FeeRupiah: String
)

data class WithDrawData(
    val certificateFee: String,
    val assuranceFee: String,
    val sendFee: String
)
