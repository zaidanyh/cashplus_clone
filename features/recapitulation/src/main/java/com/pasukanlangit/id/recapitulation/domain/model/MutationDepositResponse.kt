package com.pasukanlangit.id.recapitulation.domain.model

data class MutationDepositResponse(
    val rowNum: String,
    val trxDate: String,
    val value: String,
    val isDB: Boolean,
    val endingBalance: String,
    val productCode: String,
    val dest: String,
    val productDesc: String,
    val providerCode: String,
    val category: String
)
