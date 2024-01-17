package com.pasukanlangit.cashplus.domain.model

import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse

data class OmniPackageOrderResponse(
    val paymentCode: String,
    val transaction: TransactionTAGResponse
)