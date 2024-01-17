package com.pasukanlangit.id.cash_transfer.domain.model

data class BankRequest(
    val uuid: String,
    val bank_code: String,
    val bank_acc_num: String,
    val bank_acc_name: String? = null
)
