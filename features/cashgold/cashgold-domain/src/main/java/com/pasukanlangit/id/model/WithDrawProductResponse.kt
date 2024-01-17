package com.pasukanlangit.id.model

data class WithDrawProductResponse(
    val provider: List<WithDrawProvider>
)

data class WithDrawProduct(
    val denominationRaw: Double,
    val denominationGram: String,
    val feeRaw: Long,
    val feeIdr: String,
    val amount: Int,
    val withDrawDaily: Int,
    val withDrawLimit: Int,
    var isStateInputActive: Boolean = false,
    var inputQty: Int = 1
)

data class WithDrawProvider(
    val id: Int,
    val img: String,
    val title: String,
    val product: List<WithDrawProduct>,
    var isStateUIActive: Boolean = false
)
