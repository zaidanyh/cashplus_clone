package com.pasukanlangit.id.recapitulation.domain.utils

import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse

data class RecapTransResponse(
    val capitalTotal: String,
    val sellingTotal: String,
    val profit: String,
    val products: List<ProfitByProductResponse>
)
