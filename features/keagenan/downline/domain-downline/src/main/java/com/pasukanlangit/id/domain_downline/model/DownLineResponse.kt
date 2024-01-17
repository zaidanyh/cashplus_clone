package com.pasukanlangit.id.domain_downline.model

data class DownLineResponse(
    val downLineItems: List<DownLineItem>?
)

data class DownLineItem(
    val userPhone: String,
    val name: String,
    val ownerName: String,
    val trxCount: String,
    val address: String,
    val downLineCount: String,
    val phoneActive: String,
    val balanceRupiah: String,
    val isActive: Boolean,
    val img_url: String,
    val markupCode: String,
    val markup: String,
    val markupPerProduct: List<ItemProductMarkup>
)

data class ItemProductMarkup(
    val productCode: String,
    val markup: String
)